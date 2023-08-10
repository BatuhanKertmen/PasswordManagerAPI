using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IUserPasswordService
    {
        public Task<UserPassword> SaveAsync(string password, User passwordOwner);
    }
}
