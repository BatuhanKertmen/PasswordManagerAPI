using PasswordManager.Models;

namespace PasswordManager.Repositories;

public interface IActivationCodeRepository
{
    ActivationCode Save(ActivationCode activationCode);
    IEnumerable<ActivationCode> GetActivationCodesByUserId(Guid userId);
    void RemoveActivationCodes(IEnumerable<ActivationCode> activationCodes);
}