using Microsoft.EntityFrameworkCore;
using PasswordManager.Database;
using PasswordManager.Models;

namespace PasswordManager.Repositories
{
    public interface IUserRepository
    {
        bool CheckEmailExists(string email);
        User SaveUSer(User email);
    }
}
