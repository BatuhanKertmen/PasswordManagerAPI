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

    public ActivationCode Save(ActivationCode activationCode)
    {
        _dbContext.ActivationCodes.Add(activationCode);
        _dbContext.SaveChanges();
        return activationCode;
    }

    public IEnumerable<ActivationCode> GetActivationCodesByUserId(Guid userId)
    {
        return _dbContext.ActivationCodes.Where(activationCode => activationCode.User.Id == userId);
    }

    public void RemoveActivationCodes(IEnumerable<ActivationCode> activationCodes)
    {
        _dbContext.ActivationCodes.RemoveRange(activationCodes);
        _dbContext.SaveChanges();
    }
}