using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IUserPasswordService
    {
        public UserPassword Save(string password, User passwordOwner);
    }
}
