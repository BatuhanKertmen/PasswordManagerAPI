using PasswordManager.Models;

namespace PasswordManager.Repositories
{
    public interface IUserPasswordRepository
    {
        UserPassword Save(UserPassword user);
    }
}
