using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IActivationCodeService
    {
        public Task<ActivationCode> SendActivationCode(User user);
        public Task<bool> ActivateAccountAsync(Guid userId, string securityToken);
    }
}
