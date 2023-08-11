namespace PasswordManager.DTO;

public class UserRegisterResponseDto
{
    public Guid Id { get; set; }
    public string CommunicationAddress { get; set; }
    public bool Active { get; set; }
}