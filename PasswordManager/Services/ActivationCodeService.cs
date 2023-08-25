using System.Security.Cryptography;
using System.Text;
using Microsoft.AspNetCore.Mvc;
using PasswordManager.Communications;
using PasswordManager.Exceptions;
using PasswordManager.Models;
using PasswordManager.Repositories;
using PasswordManager.Secrets;

namespace PasswordManager.Services
{
    public class ActivationCodeService : IActivationCodeService
    {
        private readonly IActivationCodeRepository _activationCodeRepository;
        private readonly IUserRepository _userRepository;
        private readonly ICommunicationChannel _communicationChannel;
        private readonly ISecretManager _secretManager;
        private readonly IHttpContextAccessor _accessor;
        private readonly LinkGenerator _generator;


        public ActivationCodeService(
            IActivationCodeRepository activationCodeRepository, 
            ISecretManager secretManager, 
            ICommunicationChannel communicationChannel, 
            IUserRepository userRepository, 
            IHttpContextAccessor accessor, 
            LinkGenerator generator)
        {
            _activationCodeRepository = activationCodeRepository;
            _secretManager = secretManager;
            _communicationChannel = communicationChannel;
            _userRepository = userRepository;
            _accessor = accessor;
            _generator = generator;
        }

        public async Task<bool> ActivateAccountAsync(Guid userId, string securityToken)
        {
            var activationCodes = await _activationCodeRepository.GetActivationCodesByUser_IdAsync(userId);


            var isSecurityTokenValid = false;

            var unexpiredActivationCodes =
                activationCodes.Where(activationCode => (activationCode.ExpiryDate > DateTime.UtcNow) &&
                                                        (activationCode.User.Id == userId));
            
            foreach (var activationCode in unexpiredActivationCodes)
            {
                var hashResult = await HmacSecurityToken(userId + activationCode.Code);
                if (hashResult == securityToken)
                {
                    isSecurityTokenValid = true;
                    break;
                }
            }
            
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

            var createSecurityTokenOutputWrapper = await CreateSecurityToken(user.Id);
            var activationCode = new ActivationCode
            {
                Code = createSecurityTokenOutputWrapper.Code,
                Created = DateTime.UtcNow,
                ExpiryDate = DateTime.UtcNow.Add(TimeSpan.FromDays(2)),
                User = user
            };

            await _activationCodeRepository.SaveAsync(activationCode);

            var activationLink = _generator.GetUriByAction(
                _accessor.HttpContext,
                "ActivateAccount",
                "user",
                values: new { id = user.Id, securityToken = createSecurityTokenOutputWrapper.SecurityToken });
            var mailTemplate = LoadMailTemplate(activationLink);

            var mailInfo = await _secretManager.GetMailInfo();
            if (mailInfo == null)
            {
                throw new SecretNotAvailableException();
            }
            
            string from = mailInfo.Address;
            string pass = mailInfo.Password;
            await _communicationChannel.SendMessageAsync(from, pass, user.CommunicationAddress, "Activation Link", mailTemplate);
            
            return activationCode;
        }

        private string LoadMailTemplate(string activationLink)
        {
            var html = File.ReadAllText("Templates/ActivationCodeEmailTemplate.html", Encoding.UTF8);
            var linkInjectedHtml = html.Replace("[Activation Link]", activationLink);
            return linkInjectedHtml;
        }

        private async Task<CreateSecurityTokenOutputWrapper> CreateSecurityToken(Guid userId)
        {
            var code = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));
            var unhashedSecurityToken = userId + code;

            return new CreateSecurityTokenOutputWrapper
            {
                Code = code,
                SecurityToken = await HmacSecurityToken(unhashedSecurityToken)
            };
        }

        private async Task<string> HmacSecurityToken(string token)
        {
            var secretKey = await _secretManager.GetHmacPrivateKey();
            if (secretKey == null)
            {
                throw new SecretNotAvailableException();
            }
            
            byte[] securityTokenBytes;
            using (var hmac = new HMACSHA256(secretKey))
            {
                securityTokenBytes = hmac.ComputeHash(Encoding.UTF8.GetBytes(token));
            }

            return Convert.ToHexString(securityTokenBytes);
        }
    }

    public class CreateSecurityTokenOutputWrapper
    {
        public string Code { get; set; }
        public string SecurityToken { get; set; }
    }
}
