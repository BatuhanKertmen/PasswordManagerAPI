using Microsoft.EntityFrameworkCore;
using PasswordManager.Database;
using PasswordManager.Models;

namespace PasswordManager.Repositories;

public class ActivationCodeRepository : IActivationCodeRepository
{
    private readonly AppDbContext _dbContext;

    public ActivationCodeRepository(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<ActivationCode> SaveAsync(ActivationCode activationCode)
    {
        await _dbContext.ActivationCodes.AddAsync(activationCode);
        await _dbContext.SaveChangesAsync();
        return activationCode;
    }

    public async Task<List<ActivationCode>> GetActivationCodesByUser_IdAsync(Guid userId)
    {
        var activationCodes = _dbContext.ActivationCodes;
        return await _dbContext.ActivationCodes.Where(activationCode => activationCode.User.Id == userId).ToListAsync();
    }

    public async Task RemoveActivationCodes(IEnumerable<ActivationCode> activationCodes)
    {
        _dbContext.ActivationCodes.RemoveRange(activationCodes);
        await _dbContext.SaveChangesAsync();
    }
}