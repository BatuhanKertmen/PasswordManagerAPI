using PasswordManager.Database;
using PasswordManager.Models;

namespace PasswordManager.Repositories;

public class LoginInformationPasswordRepository : ILoginInformationPasswordRepository
{
    private readonly AppDbContext _context;

    public LoginInformationPasswordRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<LoginInformationPassword> SaveAsync(LoginInformationPassword loginInformationPassword)
    {
        await _context.LoginInformationPasswords.AddAsync(loginInformationPassword);
        await _context.SaveChangesAsync();
        return loginInformationPassword;
    }
}