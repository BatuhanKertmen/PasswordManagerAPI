using System.ComponentModel.DataAnnotations;

namespace PasswordManager.Models
{
    public class UserPassword
    {
        [Key]
        public Guid Id { get; set; }
        public byte[] Password { get; set; }
        public byte[] Salt { get; set; }
        public int MemorySize { get; set; }
        public int DegreeOfParallism { get; set; }
        public int Iterations { get; set; }
        public byte[] Nonce { get; set; }
        public byte[] Tag { get; set; }

        public Guid UserId { get; set; }
        public User User { get; set; }

    }
}
