using System.Net.Http.Json;
using System.Text.Json;

namespace PasswordManager.Secrets.FileBased;

class FileBasedSecretManager : ISecretManager
{
    private readonly string _secretFile = "PasswordManager.Secrets/FileBased/Secrets.json";
    private async Task<SecretModel?> ParseJsonFile()
    {
        await using var stream = File.OpenRead(_secretFile);
        return await JsonSerializer.DeserializeAsync<SecretModel>(stream);
    }
    
    public async Task<string?> GetPasswordManagerDatabaseConnectionString()
    {
        var secrets = await ParseJsonFile();
        return secrets?.DatabaseConnectionString;
    }

    public async Task<byte[]?> GetPepperSymmetricKey()
    {
        var secrets = await ParseJsonFile();
        return secrets?.PepperKey != null ? Convert.FromHexString(secrets.PepperKey) : null;
    }

    public async Task<byte[]?> GetHmacPrivateKey()
    {
        var secrets = await ParseJsonFile();
        return secrets?.PepperKey != null ? Convert.FromHexString(secrets.HmacKey) : null;
    }

    public async Task<JwtInfo?> GetJwtInfo()
    {
        var secrets = await ParseJsonFile();
        return secrets?.JwtInfo;
    }

    public async Task<MailInfo?> GetMailInfo()
    {
        var secrets = await ParseJsonFile();
        return secrets?.MailInfo;
    }
}