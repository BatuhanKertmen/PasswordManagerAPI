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
        private readonly IActivationCodeService _activationCodeService;

        public RegisterFacade(
            IUserService userService, 
            IUserPasswordService userPasswordService, 
            IActivationCodeService activationCodeService)
        {
            _userService = userService;
            _userPasswordService = userPasswordService;
            _activationCodeService = activationCodeService;
        }

        public UserResponse RegisterUser(CreateUserRequest request)
        {
            User user = _userService.Register(request.Email);
            _userPasswordService.Save(request.Password, user);

            _activationCodeService.SendActivationCode(user);

            return UserMapper.ToUserResponse(user);
        }

        public bool ActivateAccount(Guid id, string securityToken)
        {
            return _activationCodeService.ActivateAccount(id, securityToken);
        }
    }
}
