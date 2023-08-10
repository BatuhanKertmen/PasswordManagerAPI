namespace PasswordManager.Models
{
    public class LoginInformation
    {

        public Guid Id { get; set; }
        public string UsernameEncrypted { get; set; }
        public string PasswordEncrypted { get; set; }

        public User User { get; set; }
    }
}
