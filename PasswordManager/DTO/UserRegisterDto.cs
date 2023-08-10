using System.ComponentModel.DataAnnotations;

namespace PasswordManager.DTO;

public class UserRegisterDto
{
    [Required]
    public string CommunicationAddress { get; set; }
    
    [Required]
    [DataType(DataType.Password)]
    public string Password { get; set; }
}