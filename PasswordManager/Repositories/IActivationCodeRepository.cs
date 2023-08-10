using PasswordManager.Models;

namespace PasswordManager.Repositories;

public interface IActivationCodeRepository
{
    Task<ActivationCode> SaveAsync(ActivationCode activationCode);
    Task<List<ActivationCode>> GetActivationCodesByUser_IdAsync(Guid userId);
    Task RemoveActivationCodes(IEnumerable<ActivationCode> activationCodes);
}