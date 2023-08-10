namespace PasswordManager.Models
{
    public class User
    {
        public Guid Id { get; set; }
        public string CommunicationAddress { get; set; }
        public bool Active { get; set; }
        public DateTime Created { get; set; }
        public DateTime LastUpdated { get; set; }

        public ICollection<ActivationCode> ActivationCodes { get; set; }
        public ICollection<LoginInformation> LoginInformations { get; set; }
        public UserPassword UserPassword { get; set; }
    }
}
