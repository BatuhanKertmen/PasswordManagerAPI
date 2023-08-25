using PasswordManager.Secrets.FileBased;

namespace PasswordManager.Secrets;


public interface ISecretManager
{
    public Task<string?> GetPasswordManagerDatabaseConnectionString();
    
    public Task<byte[]?> GetPepperSymmetricKey();

    public Task<byte[]?> GetHmacPrivateKey();

    public Task<JwtInfo?> GetJwtInfo();

    public Task<MailInfo?> GetMailInfo();
}