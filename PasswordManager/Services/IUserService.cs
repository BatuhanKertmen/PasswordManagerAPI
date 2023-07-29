using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IUserService
    {
        public User Register(string email);
    }
}
