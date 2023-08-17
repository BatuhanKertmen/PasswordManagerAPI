using System.Security.Cryptography;
using System.Text;
using Microsoft.AspNetCore.Mvc;
using PasswordManager.Communications;
using PasswordManager.Models;
using PasswordManager.Repositories;

namespace PasswordManager.Services
{
    public class ActivationCodeService : IActivationCodeService
    {
        private readonly IActivationCodeRepository _activationCodeRepository;
        private readonly IUserRepository _userRepository;
        private readonly ICommunicationChannel _communicationChannel;
        private readonly IConfiguration _configuration;
        private readonly IHttpContextAccessor _accessor;
        private readonly LinkGenerator _generator;


        public ActivationCodeService(
            IActivationCodeRepository activationCodeRepository, 
            IConfiguration configuration, 
            ICommunicationChannel communicationChannel, 
            IUserRepository userRepository, 
            IHttpContextAccessor accessor, 
            LinkGenerator generator)
        {
            _activationCodeRepository = activationCodeRepository;
            _configuration = configuration;
            _communicationChannel = communicationChannel;
            _userRepository = userRepository;
            _accessor = accessor;
            _generator = generator;
        }

        public async Task<bool> ActivateAccountAsync(Guid userId, string securityToken)
        {
            var activationCodes = await _activationCodeRepository.GetActivationCodesByUser_IdAsync(userId);
            
            var isSecurityTokenValid = activationCodes.Any(activationCode => 
                activationCode.ExpiryDate > DateTime.UtcNow && 
                HmacSecurityToken(userId + activationCode.Code) == securityToken);

            if (!isSecurityTokenValid)
            {
                return false;
            }

            var user = await _userRepository.ActivateAccountAsync(userId);
            return user != null;
        }

        public async Task<ActivationCode> SendActivationCode(User user)
        {
            var activationCodes = await _activationCodeRepository.GetActivationCodesByUser_IdAsync(user.Id);
            await _activationCodeRepository.RemoveActivationCodes(activationCodes);

            var securityToken = CreateSecurityToken(user.Id, out string code);
            var activationCode = new ActivationCode
            {
                Code = code,
                Created = DateTime.UtcNow,
                ExpiryDate = DateTime.UtcNow.Add(TimeSpan.FromDays(2)),
                User = user
            };

            await _activationCodeRepository.SaveAsync(activationCode);

            var activationLink = _generator.GetUriByAction(
                _accessor.HttpContext,
                "ActivateAccount",
                "user",
                values: new { id = user.Id, securityToken = securityToken });
            var mailTemplate = LoadMailTemplate(activationLink);


            string from = _configuration.GetValue<string>("Mail:address");
            string pass = _configuration.GetValue<string>("Mail:password");
            await _communicationChannel.SendMessageAsync(from, pass, user.CommunicationAddress, "Activation Link", mailTemplate);
            
            return activationCode;
        }

        private string LoadMailTemplate(string activationLink)
        {
            var html = File.ReadAllText("Templates/ActivationCodeEmailTemplate.html", Encoding.UTF8);
            var linkInjectedHtml = html.Replace("[Activation Link]", activationLink);
            return linkInjectedHtml;
        }

        private string CreateSecurityToken(Guid userId, out string code)
        {
            code = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));
            var unhashedSecurityToken = userId + code;

            return HmacSecurityToken(unhashedSecurityToken);
        }

        private string HmacSecurityToken(string token)
        {
            byte[] securityTokenBytes;
            using (var hmac = new HMACSHA256( Encoding.UTF8.GetBytes(_configuration.GetValue<string>("HMAC:key"))))
            {
                securityTokenBytes = hmac.ComputeHash(Encoding.UTF8.GetBytes(token));
            }

            return Convert.ToHexString(securityTokenBytes);
        }
    }
}
