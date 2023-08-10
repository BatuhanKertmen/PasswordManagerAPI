using PasswordManager.Exceptions;
using PasswordManager.Models;
using PasswordManager.Repositories;


namespace PasswordManager.Services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _userRepository;

        public UserService(IUserRepository userRepository)
        {
            _userRepository = userRepository;
        }

        public async Task<User> RegisterAsync(User user)
        {
            if (await _userRepository.CheckCommunicationAddressExistsAsync(user.CommunicationAddress))
            {
                throw new EmailAlreadyExistsException("A user with this already registered.");
            }

            user = await _userRepository.SaveUserAsync(user);

            return user;
        }

        public async Task<User> GetUser(string communicationAddress)
        {
            var user = await _userRepository.GetUserByCommunicationAddressAsync(communicationAddress);
            if (user == null)
            {
                throw new EmailOrPasswordIsIncorrectException();
            }

            return user;
        }
    }
}
