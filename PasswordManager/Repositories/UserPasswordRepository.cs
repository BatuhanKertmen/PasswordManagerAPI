using Microsoft.EntityFrameworkCore;
using PasswordManager.Database;
using PasswordManager.Models;

namespace PasswordManager.Repositories
{
    public class UserPasswordRepository : IUserPasswordRepository
    {
        private readonly AppDbContext _context;

        public UserPasswordRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<UserPassword> SaveAsync(UserPassword userPassword)
        {
            await _context.UserPasswords.AddAsync(userPassword);
            await _context.SaveChangesAsync();
            return userPassword;
        }

        public async Task<UserPassword?> GetAsync(Guid userId)
        {
            return await _context.UserPasswords.FirstOrDefaultAsync(userPassword => userPassword.UserId == userId);
        }
    }
}
