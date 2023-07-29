using PasswordManager.Exceptions;
using PasswordManager.Mappers;
using PasswordManager.Models;
using PasswordManager.Repositories;


namespace PasswordManager.Services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _userRepository;

        public UserService(IUserRepository userRepository, IConfiguration configuration)
        {
            _userRepository = userRepository;
        }

        public User Register(string email)
        {
            if (_userRepository.CheckEmailExists(email))
            {
                throw new EmailAlreadyExistsException("A user with this already registered.");
            }

            User user = _userRepository.SaveUSer(UserMapper.ToUser(email));

            return user;
        }
    }
}
