using System.ComponentModel.DataAnnotations;

namespace PasswordManager.DTO;

public class LoginInformationResponseDto
{
    [Required]
    public string Domain { get; set; }
}