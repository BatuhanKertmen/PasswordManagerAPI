namespace PasswordManager.Exceptions;

public class EmailOrPasswordIsIncorrectException : Exception
{
    public EmailOrPasswordIsIncorrectException(string message = "Email or Password is incorrect!") : base(message) { }
}