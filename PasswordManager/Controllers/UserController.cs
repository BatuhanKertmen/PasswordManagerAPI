using Microsoft.AspNetCore.Mvc;
using PasswordManager.Facades;
using PasswordManager.DTO;

namespace PasswordManager.Controllers
{
    [Controller]
    [Route("/api/[controller]")]
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

            return CreatedAtAction(
                nameof(GetUser),
                new { id = response.Id },
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

        [HttpGet("activate/{id:guid}/{securityToken}")]
        public async Task<IActionResult> ActivateAccount([FromRoute] Guid id, [FromRoute] string securityToken)
        {
            var successful = await _userActionsFacade.ActivateAccountAsync(id, securityToken);
            if (!successful)
            {
                return BadRequest();
            }

            return Ok();
        }
        
        [HttpGet("{id:guid}")]
        public IActionResult GetUser(Guid id)
        {
            throw new NotImplementedException();
        }
    }
}
