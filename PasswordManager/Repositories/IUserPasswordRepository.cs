using PasswordManager.Models;

namespace PasswordManager.Repositories
{
    public interface IUserPasswordRepository
    {
        Task<UserPassword> SaveAsync(UserPassword user);
        Task<UserPassword?> GetUserPasswordByUser_IdAsync(Guid userId);
    }
}
