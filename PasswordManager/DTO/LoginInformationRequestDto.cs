using System.ComponentModel.DataAnnotations;

namespace PasswordManager.DTO;

public class LoginInformationRequestDto
{
    [Required]
    public string UserName { get; set; }
    
    [Required]
    [DataType(DataType.Password)]
    public string Password { get; set; }
    
    [Required]
    public string Domain { get; set; }
    
    [Required]
    [RegularExpression("[0-9A-F]64")]
    public string SaltHexString { get; set; }
    
    [Required]
    [Range(1,1024)]
    public int MemorySize { get; set; }

    [Required]
    [Range(1, 32)]
    public int DegreeOfParallelism { get; set; }

    [Required]
    [Range(1, 32)]
    public int Iterations { get; set; }
}