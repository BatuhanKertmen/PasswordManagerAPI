using PasswordManager.Contracts;
using PasswordManager.Models;
using System.Text;

namespace PasswordManager.Mappers
{
    public class UserMapper
    {
        public static User ToUser(string email)
        {
            return new User()
            {
                Id = Guid.NewGuid(),
                CommunicationAddress = email,
                Active = false,
                Created = DateTime.UtcNow,
                LastUpdated = DateTime.UtcNow
            };
        }

        public static UserResponse ToUserResponse(User user)
        {
            return new UserResponse(
                user.Id,
                user.CommunicationAddress,
                user.Active,
                user.Created,
                user.LastUpdated
            );
        }
    }
}
