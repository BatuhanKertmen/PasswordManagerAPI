﻿using Microsoft.AspNetCore.Mvc;
using PasswordManager.Facades;
using PasswordManager.DTO;

namespace PasswordManager.Controllers
{
    [Controller]
    [Route("/api/v1/[controller]")]
    public class UserController : ControllerBase
    {
        private readonly UserActionsFacade _userActionsFacade;

        public UserController(UserActionsFacade userActionsFacade)
        {
            _userActionsFacade = userActionsFacade;
        }

        [HttpPost("register")]
        public async Task<IActionResult> RegisterAsync([FromBody] UserRegisterRequestDto request)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var response = await _userActionsFacade.RegisterAsync(request);

            return CreatedAtRoute(
                "",
                null,
                response
            );
        }

        [HttpPost("login")]
        public async Task<IActionResult> LoginAsync([FromBody] UserLoginRequestDto request)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var response = await _userActionsFacade.LoginAsync(request);

            return Ok(response);
        }

        [HttpGet]
        [Route("activate")]
        public async Task<IActionResult> ActivateAccount([FromQuery] Guid id, [FromQuery] string securityToken)
        {
            var successful = await _userActionsFacade.ActivateAccountAsync(id, securityToken);
            if (!successful)
            {
                return BadRequest();
            }

            return Ok();
        }

        [HttpGet]
        public async Task<IActionResult> GetUserPasswordHashInfoAsync([FromQuery] string communicationAddress)
        {
            var userPasswordHashInfo = await _userActionsFacade.GetUserPasswordHashInfoAsync(communicationAddress);
            return Ok(userPasswordHashInfo);
        }
    }
}
