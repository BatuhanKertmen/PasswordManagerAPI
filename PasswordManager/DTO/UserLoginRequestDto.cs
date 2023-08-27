using System.ComponentModel.DataAnnotations;

namespace PasswordManager.DTO;

public class UserLoginRequestDto
{
    [Required(ErrorMessage = "Email is required")]
    [EmailAddress(ErrorMessage = "Invalid email format")]
    public string CommunicationAddress { get; set; }
    
    [Required(ErrorMessage = "Password is required")]
    [RegularExpression("[a-zA-Z0-9]{256}", ErrorMessage = "Password 128 byte hex-string")]
    public string Password { get; set; }
}