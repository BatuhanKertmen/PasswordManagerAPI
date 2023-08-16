using System.ComponentModel.DataAnnotations;

namespace PasswordManager.DTO;

public class AddLoginInformationResponseDto
{
    [Required]
    public string Domain { get; set; }
}