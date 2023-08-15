using System.Text;
using AutoMapper;
using Microsoft.Net.Http.Headers;
using PasswordManager.DTO;
using PasswordManager.Models;
using PasswordManager.Services;

namespace PasswordManager.Facades;

public class LoginInformationFacade
{
    private readonly IJwtService _jwtService;
    private readonly IUserService _userService;
    private readonly ILoginInformationService _loginInformationService;
    private readonly ILoginInformationPasswordService _loginInformationPasswordService;
    private readonly IMapper _mapper;

    public LoginInformationFacade(
        IJwtService jwtService, 
        IUserService userService, 
        ILoginInformationService loginInformationService, 
        ILoginInformationPasswordService loginInformationPasswordService,
        IMapper mapper)
    {
        _jwtService = jwtService;
        _userService = userService;
        _loginInformationService = loginInformationService;
        _loginInformationPasswordService = loginInformationPasswordService;
        _mapper = mapper;
    }

    public async Task<LoginInformationResponseDto> SaveAsync(HttpRequest request, LoginInformationRequestDto data)
    {
        var userId = _jwtService.GetUserId(request.Headers[HeaderNames.Authorization]);
        var user = await _userService.GetUserAsync(userId);
        
        var loginInformation = _mapper.Map<LoginInformation>(data);
        loginInformation.User = user;

        var loginInformationPassword = _mapper.Map<LoginInformationPassword>(data); 
        loginInformation.LoginInformationPassword = loginInformationPassword;

        loginInformation = await _loginInformationService.SaveAsync(loginInformation);
        await _loginInformationPasswordService.SaveAsync(loginInformationPassword);

        return _mapper.Map<LoginInformationResponseDto>(loginInformation);
    }
}