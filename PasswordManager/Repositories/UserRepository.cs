using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PasswordManager.Database;
using PasswordManager.Models;

namespace PasswordManager.Repositories
{
    public class UserRepository : IUserRepository
    {
        private readonly AppDbContext _context;

        public UserRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<bool> CheckCommunicationAddressExistsAsync(string email)
        {
            return await _context.Users.AnyAsync(user => user.CommunicationAddress == email);
        }

        public async Task<User> SaveUserAsync(User user)
        {
            await _context.Users.AddAsync(user);
            await _context.SaveChangesAsync();
            return user;
        }

        public async Task<User?> ActivateAccountAsync(Guid userId)
        {
            var user =  await _context.Users.FindAsync(userId);
            if (user == null || user.Active)
            {
                return null;
            }

            user.Active = true;
            await _context.SaveChangesAsync();

            return user;
        }

        public async Task<User?> GetUserByCommunicationAddressAsync(string address)
        {
            return await _context.Users.FirstOrDefaultAsync(x => x.CommunicationAddress == address);
        }
    }
}
