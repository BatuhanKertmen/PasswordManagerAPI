using PasswordManager.Models;

namespace PasswordManager.Services;

public interface ILoginInformationService
{
    Task<LoginInformation> SaveAsync(LoginInformation loginInformation);
    Task<IEnumerable<LoginInformation>> GelAllByDomainAsync(string domain);
}