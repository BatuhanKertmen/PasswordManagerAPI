using Microsoft.EntityFrameworkCore;
using PasswordManager.Database;
using PasswordManager.Models;

namespace PasswordManager.Repositories;

public class LoginInformationRepository : ILoginInformationRepository
{
    private readonly AppDbContext _context;

    public LoginInformationRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<LoginInformation> SaveAsync(LoginInformation loginInformation)
    {
        await _context.LoginInformation.AddAsync(loginInformation);
        await _context.SaveChangesAsync();
        return loginInformation;
    }

    public async Task<List<LoginInformation>> GetAllByDomainAsync(string domain)
    {
        return await _context.LoginInformation.Where(info => info.Domain == domain)
            .Include(info => info.LoginInformationPassword)
            .ToListAsync();
    }
}