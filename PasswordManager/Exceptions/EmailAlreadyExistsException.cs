namespace PasswordManager.Exceptions
{
    public class EmailAlreadyExistsException : Exception
    {
        public EmailAlreadyExistsException(string message = "This email already exists!") : base(message) { }
    }
}
