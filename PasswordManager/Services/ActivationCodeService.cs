using System.Security.Cryptography;
using System.Text;
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

        public ActivationCodeService(IActivationCodeRepository activationCodeRepository, IConfiguration configuration, ICommunicationChannel communicationChannel, IUserRepository userRepository)
        {
            _activationCodeRepository = activationCodeRepository;
            _configuration = configuration;
            _communicationChannel = communicationChannel;
            _userRepository = userRepository;
        }

        public bool ActivateAccount(Guid userId, string securityToken)
        {
            IEnumerable<ActivationCode> activationCodes = _activationCodeRepository.GetActivationCodesByUserId(userId);
            
            bool isSecurityTokenValid = activationCodes.Any(activationCode => 
                activationCode.ExpiryDate > DateTime.UtcNow && 
                HmacSecurityToken(userId + activationCode.Code) == securityToken);

            if (!isSecurityTokenValid)
            {
                return false;
            }

            User? user = _userRepository.ActivateAccount(userId);
            return user != null;
        }

        public ActivationCode SendActivationCode(User user)
        {
            List<ActivationCode> activationCodes = _activationCodeRepository.GetActivationCodesByUserId(user.Id).ToList();
            _activationCodeRepository.RemoveActivationCodes(activationCodes);

            string securityToken = CreateSecurityToken(user.Id, out string code);
            ActivationCode activationCode = new ActivationCode
            {
                Code = code,
                Created = DateTime.UtcNow,
                ExpiryDate = DateTime.UtcNow.Add(TimeSpan.FromDays(2)),
                User = user
            };

            _activationCodeRepository.Save(activationCode);
            
            string activationLink = $"https://localhost:7186/activate/{user.Id}/{securityToken}";
            string mailTemplate = LoadMailTemplate(activationLink);
            
            _communicationChannel.SendMessage(user.CommunicationAddress, mailTemplate);
            
            return activationCode;
        }

        private string LoadMailTemplate(string activationLink)
        {
            string html = File.ReadAllText("Templates/ActivationCodeEmailTemplate.html", Encoding.UTF8);
            string linkInjectedHtml = html.Replace("[Activation Link]", activationLink);
            return linkInjectedHtml;
        }

        private string CreateSecurityToken(Guid userId, out string code)
        {
            code = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));
            string unhashedSecurityToken = userId + code;

            return HmacSecurityToken(unhashedSecurityToken);
        }

        private string HmacSecurityToken(string token)
        {
            byte[] securityTokenBytes;
            using (HMACSHA256 hmac = new HMACSHA256( Encoding.UTF8.GetBytes(_configuration.GetValue<string>("HMAC:key"))))
            {
                securityTokenBytes = hmac.ComputeHash(Encoding.UTF8.GetBytes(token));
            }

            return Convert.ToHexString(securityTokenBytes);
        }
    }
}
