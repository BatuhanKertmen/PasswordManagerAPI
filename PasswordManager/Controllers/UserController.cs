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
        public async Task<IActionResult> Register(UserRegisterDto request)
        {
            var user = _mapper.Map<User>(request);
            var response = await _userActionsFacade.RegisterUserAsync(request);

            return CreatedAtAction(
                nameof(GetUser),
                new { id = response.Id },
                response
            );
        }

        [HttpGet("activate/{id:guid}/{securityToken}")]
        public async Task<IActionResult> ActivateAccount([FromRoute] Guid id, [FromRoute] string securityToken)
        {
            var successful = await _userActionsFacade.ActivateAccountAsync(id, securityToken);
            if (!successful)
            {
                return BadRequest("Could Not Activate Account!");
            }

            return Ok("Account Activated");
        }

        [HttpGet("{id:guid}")]
        public IActionResult GetUser(Guid id)
        {
            throw new NotImplementedException();
        }
    }
}
