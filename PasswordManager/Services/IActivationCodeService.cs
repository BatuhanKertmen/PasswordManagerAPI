using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IActivationCodeService
    {
        public ActivationCode SendActivationCode(User user);
        public bool ActivateAccount(Guid userId, string securityToken);
    }
}
