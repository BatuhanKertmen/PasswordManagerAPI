namespace PasswordManager.DTO;

public class GetLoginInformationResponseDto
{
    public string UsernameEncrypted { get; set; }
    public string PasswordEncrypted { get; set; }
    public string SaltHexString { get; set; }
    public int MemorySize { get; set; }
    public int DegreeOfParallelism { get; set; }
    public int Iterations { get; set; }
}