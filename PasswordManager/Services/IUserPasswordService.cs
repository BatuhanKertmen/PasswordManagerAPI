using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IUserPasswordService
    {
        public Task<UserPassword> SaveAsync(string password, User passwordOwner);
        Task<bool> CheckPasswordAsync(Guid userId, string password);
    }
}
