using PasswordManager.Secrets.FileBased;

namespace PasswordManager.Secrets;


public interface ISecretManager
{
    public string? GetPasswordManagerDatabaseConnectionString();
    
    public byte[]? GetPepperSymmetricKey();

    public byte[]? GetHmacPrivateKey();

    public JwtInfo? GetJwtInfo();
    public byte[]? GetJwtKey();

    public MailInfo? GetMailInfo();
}