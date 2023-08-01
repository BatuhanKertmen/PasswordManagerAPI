using Microsoft.AspNetCore.Mvc;
using PasswordManager.Contracts;
using PasswordManager.Facades;
using PasswordManager.Mappers;
using PasswordManager.Models;
using PasswordManager.Services;
using System.Text;

namespace PasswordManager.Controllers
{
    [Controller]
    [Route("/api/[controller]")]
    public class UserController : ControllerBase
    {
        private readonly RegisterFacade _registerFacade;

        public UserController(RegisterFacade registerFacade)
        {
            _registerFacade = registerFacade;
        }

        [HttpPost("register")]
        public IActionResult Register(CreateUserRequest request)
        {
            UserResponse response = _registerFacade.RegisterUser(request);

            return CreatedAtAction(
                nameof(GetUser),
                new { id = response.Id },
                response
            );
        }

        [HttpGet("activate/{id:guid}/{securityToken}")]
        public IActionResult ActivateAccount([FromRoute] Guid id, [FromRoute] string securityToken)
        {
            bool successful = _registerFacade.ActivateAccount(id, securityToken);
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
