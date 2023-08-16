using PasswordManager.Models;

namespace PasswordManager.Repositories;

public interface ILoginInformationPasswordRepository
{
    Task<LoginInformationPassword> SaveAsync(LoginInformationPassword loginInformationPassword);
}