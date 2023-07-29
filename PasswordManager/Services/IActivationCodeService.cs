using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IActivationCodeService
    {
        public ActivationCode SendActivationCode();
        public bool ActivateAccount();
    }
}
