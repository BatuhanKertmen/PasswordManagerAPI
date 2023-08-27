using System.Text.Json;

namespace PasswordManager.Secrets.FileBased;

public class FileBasedSecretManager : ISecretManager
{
    private const string SecretFile = "../PasswordManager.Secrets/FileBased/Secrets.json";

    private SecretModel? ParseJsonFile()
    {
        using var stream = File.OpenRead(SecretFile);
        return JsonSerializer.Deserialize<SecretModel>(stream);
    }
    
    public string? GetPasswordManagerDatabaseConnectionString()
    {
        var secrets = ParseJsonFile();
        return secrets?.DatabaseConnectionString;
    }

    public byte[]? GetPepperSymmetricKey()
    {
        var secrets = ParseJsonFile();
        return secrets?.PepperKey != null ? Convert.FromHexString(secrets.PepperKey) : null;
    }

    public byte[]? GetHmacPrivateKey()
    {
        var secrets = ParseJsonFile();
        return secrets?.PepperKey != null ? Convert.FromHexString(secrets.HmacKey) : null;
    }

    public JwtInfo? GetJwtInfo()
    {
        var secrets = ParseJsonFile();
        return secrets?.JwtInfo;
    }

    public byte[]? GetJwtKey()
    {
        var secret = ParseJsonFile();
        return secret != null ? Convert.FromHexString(secret.JwtInfo.JwtKeyHexString) : null;
    }

    public MailInfo? GetMailInfo()
    {
        var secrets = ParseJsonFile();
        return secrets?.MailInfo;
    }
}