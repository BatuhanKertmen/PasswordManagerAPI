using PasswordManager.Models;

namespace PasswordManager.Services
{
    public interface IUserService
    {
        public Task<User> RegisterAsync(User email);
        public Task<User> GetUser(string communicationAddress);
    }
}
