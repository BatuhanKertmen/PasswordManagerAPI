using PasswordManager.Database;
using PasswordManager.Models;
using PasswordManager.Services;

namespace PasswordManager.Repositories;

public class LoginInformationPasswordService : ILoginInformationPasswordService
{
    private readonly ILoginInformationPasswordRepository _loginInformationPasswordRepository;

    public LoginInformationPasswordService(ILoginInformationPasswordRepository loginInformationPasswordRepository)
    {
        _loginInformationPasswordRepository = loginInformationPasswordRepository;
    }

    public async Task<LoginInformationPassword> SaveAsync(LoginInformationPassword loginInformationPassword)
    {
        loginInformationPassword = await _loginInformationPasswordRepository.SaveAsync(loginInformationPassword);
        return loginInformationPassword;
    }
}