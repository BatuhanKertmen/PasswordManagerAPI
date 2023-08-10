using System.Net;
using System.Net.Mail;

namespace PasswordManager.Communications;

public class Mail : ICommunicationChannel
{
    
    public async Task SendMessageAsync(string from, string pass, string to, string subject, string message)
    {
        string senderEmail = from;
        string senderPassword = pass;

        // Recipient's email address
        string recipientEmail = to;

        // SMTP server and port for your email provider
        string smtpServer = "smtp.gmail.com";
        int smtpPort = 587; // 587 for TLS,

        // Create the email message
        MailMessage mail = new MailMessage(senderEmail, recipientEmail)
        {
            Subject = subject,
            Body = message,
            IsBodyHtml = true
        };

        // Setup the SMTP client
        SmtpClient smtpClient = new SmtpClient(smtpServer)
        {
            Port = smtpPort,
            Credentials = new NetworkCredential(senderEmail, senderPassword),
            EnableSsl = true // Set to true for SSL, false for TLS
        };
        
        // Send the email
        smtpClient.Send(mail);
    }
}