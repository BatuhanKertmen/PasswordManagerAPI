using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PasswordManager.Database;
using PasswordManager.Models;

namespace PasswordManager.Repositories
{
    public interface IUserRepository
    {
        Task<bool> CheckCommunicationAddressExistsAsync(string email);
        Task<User> SaveUserAsync(User email);
        Task<User?> ActivateAccountAsync(Guid userId);
        Task<User?> GetUserByCommunicationAddressAsync(string address);
    }
}
