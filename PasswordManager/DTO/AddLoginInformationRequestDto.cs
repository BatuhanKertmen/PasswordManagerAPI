using System.ComponentModel.DataAnnotations;

namespace PasswordManager.DTO;

public class AddLoginInformationRequestDto
{
    [Required]
    public string UserName { get; set; }
    
    [Required]
    [DataType(DataType.Password)]
    public string Password { get; set; }
    
    [Required]
    public string Domain { get; set; }
    
    [Required]
    [RegularExpression("[0-9A-Fa-f]{64}", ErrorMessage = "Given salt is not a 32 byte hex string")]
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