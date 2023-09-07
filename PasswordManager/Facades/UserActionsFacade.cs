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

        public async Task<UserRegisterResponseDto> RegisterAsync(UserRegisterRequestDto request)
        {
            var user = await _userService.RegisterAsync(_mapper.Map<User>(request));
            await _userPasswordService.SaveAsync(request.PasswordHexString, _mapper.Map<UserPassword>(request), user);

            await _activationCodeService.SendActivationCode(user);

            return _mapper.Map<UserRegisterResponseDto>(user);
        }

        public async Task<bool> ActivateAccountAsync(Guid id, string securityToken)
        {
            return await _activationCodeService.ActivateAccountAsync(id, securityToken);
        }

        public async Task<UserLoginResponseDto> LoginAsync(UserLoginRequestDto request)
        {
            var user = await _userService.GetUserAsync(request.CommunicationAddress);
            var isPasswordCorrect = await _userPasswordService.CheckPasswordAsync(user.Id, request.Password);

            if (isPasswordCorrect == false)
            {
                throw new EmailOrPasswordIsIncorrectException();
            }
            
            var token = _jwtService.CreateJwtToken(user);
            return _mapper.Map<UserLoginResponseDto>(token);
        }

        public async Task<GetUserPasswordHashInfoResponseDto> GetUserPasswordHashInfoAsync(string communicationAddress)
        {
            var user = await _userService.GetUserAsync(communicationAddress);
            return _mapper.Map<GetUserPasswordHashInfoResponseDto>(user.UserPassword);
        }
    }
}
