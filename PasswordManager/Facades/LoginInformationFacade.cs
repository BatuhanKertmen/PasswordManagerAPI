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

    public async Task<AddLoginInformationResponseDto> SaveAsync(HttpRequest request, AddLoginInformationRequestDto data)
    {
        var userId = _jwtService.GetUserId(request.Headers[HeaderNames.Authorization]);
        var user = await _userService.GetUserAsync(userId);
        
        var loginInformation = _mapper.Map<LoginInformation>(data);
        loginInformation.User = user;
        
        loginInformation = await _loginInformationService.SaveAsync(loginInformation);
        

        var loginInformationPassword = _mapper.Map<LoginInformationPassword>(data);
        loginInformationPassword.LoginInformationId = loginInformation.Id;
        loginInformationPassword.LoginInformation = loginInformation;
        
        loginInformation.LoginInformationPassword = loginInformationPassword;
        await _loginInformationPasswordService.SaveAsync(loginInformationPassword);

        return _mapper.Map<AddLoginInformationResponseDto>(loginInformation);
    }

    public async Task<IEnumerable<GetLoginInformationResponseDto>> GetAllByDomainAsync(GetLoginInformationRequestDto request)
    {
        var informationList = await _loginInformationService.GelAllByDomainAsync(request.Domain);
        return informationList
            .Select(item => _mapper.Map<GetLoginInformationResponseDto>(item))
            .ToList();
    }
}