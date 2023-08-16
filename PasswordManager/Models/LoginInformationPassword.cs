namespace PasswordManager.Models;

public class LoginInformationPassword
{
    public Guid Id { get; set; }
    public byte[] Salt { get; set; }
    public int MemorySize { get; set; }
    public int DegreeOfParallelism { get; set; }
    public int Iterations { get; set; }

    public Guid LoginInformationId { get; set; }
    public LoginInformation LoginInformation { get; set; }
}