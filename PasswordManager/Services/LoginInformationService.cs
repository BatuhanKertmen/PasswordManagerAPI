using PasswordManager.Models;
using PasswordManager.Repositories;

namespace PasswordManager.Services;

public class LoginInformationService : ILoginInformationService
{
    private readonly ILoginInformationRepository _loginInformationRepository;

    public LoginInformationService(ILoginInformationRepository loginInformationRepository)
    {
        _loginInformationRepository = loginInformationRepository;
    }

    public async Task<LoginInformation> SaveAsync(LoginInformation loginInformation)
    {
        loginInformation = await _loginInformationRepository.SaveAsync(loginInformation);
        return loginInformation;
    }

    public async Task<List<LoginInformation>> GelAllByDomainAsync(string domain)
    {
        return await _loginInformationRepository.GetAllByDomainAsync(domain);
    }
}