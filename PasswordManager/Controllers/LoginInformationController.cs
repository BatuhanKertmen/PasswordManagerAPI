using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Xml.Linq;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Net.Http.Headers;
using PasswordManager.DTO;
using PasswordManager.Facades;

namespace PasswordManager.Controllers;

[Controller]
[Route("/api/v1/[controller]")]
[Authorize]
public class LoginInformationController : ControllerBase
{
    private readonly LoginInformationFacade _loginInformationFacade;

    public LoginInformationController(LoginInformationFacade loginInformationFacade)
    {
        _loginInformationFacade = loginInformationFacade;
    }


    [HttpPost]
    public async Task<IActionResult> SaveAsync([FromBody] AddLoginInformationRequestDto request)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }
        
        var response = await _loginInformationFacade.SaveAsync(Request, request);
        
        return Ok(response);
    }

    [HttpGet]
    public async Task<IActionResult> GetAllByDomainAsync([FromQuery] GetLoginInformationRequestDto request)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }
        
        var response = await _loginInformationFacade.GetAllByDomainAsync(request);

        return Ok(response);
    }
}