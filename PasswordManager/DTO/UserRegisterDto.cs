using System.ComponentModel.DataAnnotations;

namespace PasswordManager.DTO;

public class UserRegisterDto
{
    [Required(ErrorMessage = "Email is required")]
    [EmailAddress(ErrorMessage = "Invalid email format")]
    public string CommunicationAddress { get; set; }
    
    [Required(ErrorMessage = "Password is required")]
    [StringLength(64, MinimumLength = 8, ErrorMessage = "Password must be between 8 and 64 characters")]
    [RegularExpression(@"^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$", 
        ErrorMessage = "Password must contain at least one letter, one number, and one special character")]
    [DataType(DataType.Password)]
    public string Password { get; set; }
}