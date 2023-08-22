using System.ComponentModel.DataAnnotations;

namespace PasswordManager.DTO;

public class UserRegisterRequestDto
{
    [Required(ErrorMessage = "Email is required")]
    [EmailAddress(ErrorMessage = "Invalid email format")]
    public string CommunicationAddress { get; set; }
    
    [Required(ErrorMessage = "Password is required")]
    [RegularExpression("[a-zA-Z0-9]{256}", ErrorMessage = "Password 128 byte hex-string")]
    public string PasswordHexString { get; set; }
    
    [Required(ErrorMessage = "Salt is required")]
    [RegularExpression("[a-zA-Z0-9]{64}", ErrorMessage = "Salt 32 byte hex-string")]
    public string SaltHexString { get; set; }
    
    [Required]
    [Range(1,1024, ErrorMessage = "Given Memory size is not in the accepted range")]
    public int MemorySize { get; set; }

    [Required]
    [Range(1, 32, ErrorMessage = "Given parallel degree is not in the accepted range")]
    public int DegreeOfParallelism { get; set; }

    [Required]
    [Range(1, 32, ErrorMessage = "Given iterations is not in the accepted range")]
    public int Iterations { get; set; }
}