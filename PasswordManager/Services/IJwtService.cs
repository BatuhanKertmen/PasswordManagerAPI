using PasswordManager.Models;

namespace PasswordManager.Services;

public interface IJwtService
{
    public string CreateJwtToken(User user);
}