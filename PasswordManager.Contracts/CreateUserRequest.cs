namespace PasswordManager.Contracts
{
    public record CreateUserRequest(
        string Email,
        string Password);
}