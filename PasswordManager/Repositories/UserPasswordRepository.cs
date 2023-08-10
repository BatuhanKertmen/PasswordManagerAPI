using Microsoft.EntityFrameworkCore;
using PasswordManager.Database;
using PasswordManager.Models;

namespace PasswordManager.Repositories
{
    public class UserPasswordRepository : IUserPasswordRepository
    {
        private readonly AppDbContext _appDbContext;

        public UserPasswordRepository(AppDbContext appDbContext)
        {
            _appDbContext = appDbContext;
        }

        public async Task<UserPassword> SaveAsync(UserPassword userPassword)
        {
            await _appDbContext.UserPasswords.AddAsync(userPassword);
            await _appDbContext.SaveChangesAsync();
            return userPassword;
        }

        public async Task<UserPassword?> GetUserPasswordByUser_IdAsync(Guid userId)
        {
            var userPassword = await _appDbContext.UserPasswords.FirstOrDefaultAsync(item => item.UserId == userId);
            return userPassword;
        }
    }
}
