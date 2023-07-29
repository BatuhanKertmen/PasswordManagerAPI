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

        public UserPassword Save(UserPassword userPassword)
        {
            _appDbContext.Add(userPassword);
            _appDbContext.SaveChanges();
            return userPassword;
        }
    }
}
