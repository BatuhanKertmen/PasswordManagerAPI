using PasswordManager.Contracts;
using PasswordManager.Mappers;
using PasswordManager.Models;
using PasswordManager.Services;
using PasswordManager.Communications;

namespace PasswordManager.Facades
{
    public class RegisterFacade
    {
        private readonly IUserService _userService;
        private readonly IUserPasswordService _userPasswordService;
        private readonly ICommunicationChannel _communicationChannel;

        public RegisterFacade(IUserService userService, IUserPasswordService userPasswordService, ICommunicationChannel communicationChannel)
        {
            _userService = userService;
            _userPasswordService = userPasswordService;
            _communicationChannel = communicationChannel;
        }

        public UserResponse RegisterUser(CreateUserRequest request)
        {
            User user = _userService.Register(request.Email);
            _userPasswordService.Save(request.Password, user);


            return UserMapper.ToUserResponse(user);
        }

    }
}
