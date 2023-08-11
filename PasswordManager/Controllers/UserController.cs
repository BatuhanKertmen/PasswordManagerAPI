using Microsoft.AspNetCore.Mvc;
using PasswordManager.Facades;
using PasswordManager.Models;
using AutoMapper;
using PasswordManager.DTO;

namespace PasswordManager.Controllers
{
    [Controller]
    [Route("/api/[controller]")]
    public class UserController : ControllerBase
    {
        private readonly UserActionsFacade _userActionsFacade;
        private readonly IMapper _mapper;

        public UserController(UserActionsFacade userActionsFacade, IMapper mapper)
        {
            _userActionsFacade = userActionsFacade;
            _mapper = mapper;
        }

        [HttpPost("register")]
        public async Task<IActionResult> RegisterAsync([FromBody] UserRegisterDto request)
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
        public async Task<IActionResult> LoginAsync([FromBody] UserLoginDto request)
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
