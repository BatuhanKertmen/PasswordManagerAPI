using PasswordManager.Models;

namespace PasswordManager.Repositories;

public interface ILoginInformationRepository
{
    Task<LoginInformation> SaveAsync(LoginInformation loginInformation);
}