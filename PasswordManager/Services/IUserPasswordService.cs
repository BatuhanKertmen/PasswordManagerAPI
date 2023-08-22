using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IUserPasswordService
    {
        public Task<UserPassword> SaveAsync(string passwordHexString, UserPassword userPassword, User passwordOwner);
        Task<bool> CheckPassword(Guid userId, string password);
    }
}
