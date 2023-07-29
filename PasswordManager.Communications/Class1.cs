namespace PasswordManager.Communications
{
    public interface ICommunicationChannel
    {
        public void SendMessage(string address, string message);

    }
}