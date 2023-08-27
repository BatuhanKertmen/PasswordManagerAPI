namespace PasswordManager.Secrets.FileBased;

public class SecretModel
{
    public string DatabaseConnectionString { get; set; }
    public string HmacKey { get; set; }
    public string PepperKey { get; set; }
    public JwtInfo JwtInfo { get; set; }
    public MailInfo MailInfo { get; set; }
}

public class JwtInfo
{
    public string JwtKeyHexString { get; set; }
    public string Audience { get; set; }
    public string Issuer { get; set; }
}

public class MailInfo
{
    public string Address { get; set; }
    public string Password { get; set; }
}