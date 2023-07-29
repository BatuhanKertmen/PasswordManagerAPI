namespace PasswordManager.Contracts
{
    public record UserResponse(
        Guid Id,
        string Email,
        bool Active,
        DateTime Created,
        DateTime LastUpdated);
}
