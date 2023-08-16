using PasswordManager.Models;

namespace PasswordManager.Services;

public interface ILoginInformationPasswordService
{
    Task<LoginInformationPassword> SaveAsync(LoginInformationPassword loginInformationPassword);
}