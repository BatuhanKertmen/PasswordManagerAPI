using System.Security.Claims;
using System.Text;
using AutoMapper;
using Microsoft.IdentityModel.Tokens;
using PasswordManager.Services;
using PasswordManager.DTO;
using PasswordManager.Exceptions;
using PasswordManager.Models;

namespace PasswordManager.Facades
{
    public class UserActionsFacade
    {
        private readonly IUserService _userService;
        private readonly IUserPasswordService _userPasswordService;
        private readonly IActivationCodeService _activationCodeService;
        private readonly IMapper _mapper;
        private readonly IJwtService _jwtService;

        public UserActionsFacade(
            IUserService userService, 
            IUserPasswordService userPasswordService, 
            IActivationCodeService activationCodeService,
            IMapper mapper, 
            IJwtService jwtService)
        {
            _userService = userService;
            _userPasswordService = userPasswordService;
            _activationCodeService = activationCodeService;
            _mapper = mapper;
            _jwtService = jwtService;
        }

        public async Task<UserResponseDto> RegisterAsync(UserRegisterDto request)
        {
            var user = await _userService.RegisterAsync(_mapper.Map<User>(request));
            await _userPasswordService.SaveAsync(request.Password, user);

            await _activationCodeService.SendActivationCode(user);

            return _mapper.Map<UserResponseDto>(user);
        }

        public async Task<bool> ActivateAccountAsync(Guid id, string securityToken)
        {
            return await _activationCodeService.ActivateAccountAsync(id, securityToken);
        }

        public async Task<string?> LoginAsync(UserLoginDto request)
        {
            var user = await _userService.GetUserAsync(request.CommunicationAddress);
            var isPasswordCorrect = await _userPasswordService.CheckPassword(user.Id, request.Password);

            if (isPasswordCorrect == false)
            {
                throw new EmailOrPasswordIsIncorrectException();
            }
            
            return _jwtService.CreateJwtToken(user);
        }
    }
}
