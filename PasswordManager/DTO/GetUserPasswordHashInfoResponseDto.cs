namespace PasswordManager.DTO;

public class GetUserPasswordHashInfoResponseDto
{
    public byte[] Salt { get; set; }
    public int MemorySize { get; set; }
    public int DegreeOfParallelism { get; set; }
    public int Iterations { get; set; }
}