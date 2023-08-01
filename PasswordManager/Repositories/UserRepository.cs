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

        public bool CheckEmailExists(string email)
        {
            return _context.Users.Any(user => user.CommunicationAddress == email);
        }

        public User SaveUSer(User user)
        {
            _context.Users.Add(user);
            _context.SaveChanges();
            return user;
        }

        public User? ActivateAccount(Guid userId)
        {
            User? user = _context.Users.Find(userId);
            if (user == null || user.Active)
            {
                return null;
            }

            user.Active = true;
            _context.SaveChanges();

            return user;
        }
    }
}
