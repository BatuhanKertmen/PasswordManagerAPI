namespace PasswordManager.Communications
{
    public interface ICommunicationChannel
    {
        public Task SendMessageAsync(string from, string pass, string to, string subject, string message);

    }
}