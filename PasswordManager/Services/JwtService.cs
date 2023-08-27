using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using Microsoft.IdentityModel.Tokens;
using PasswordManager.Exceptions;
using PasswordManager.Models;
using PasswordManager.Secrets;

namespace PasswordManager.Services;

public class JwtService : IJwtService
{
    private readonly ISecretManager _secretManager;

    public JwtService(ISecretManager secretManager)
    {
        _secretManager = secretManager;
    }


    public string CreateJwtToken(User user)
    {
        var claims = new List<Claim>
        {
            new Claim(ClaimTypes.PrimarySid, user.Id.ToString())
        };

        var jwtInfo = _secretManager.GetJwtInfo();
        var jwtPrivateKey = _secretManager.GetJwtKey();
        if (jwtPrivateKey == null || jwtInfo == null)
        {
            throw new SecretNotAvailableException();
        }
        
        var key = new SymmetricSecurityKey(jwtPrivateKey);
        var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

        var token = new JwtSecurityToken(
            jwtInfo.Issuer,
            jwtInfo.Audience,
            claims,
            expires: DateTime.UtcNow.AddDays(1),
            signingCredentials: credentials
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    public Guid GetUserId(string authHeader)
    {
        var handler = new JwtSecurityTokenHandler();
        var jwtSecurityToken = handler.ReadJwtToken(authHeader.Split(" ")[1]);
        var userIdClaim = jwtSecurityToken.Claims.First(claim => claim.Type == ClaimTypes.PrimarySid);
        var userId = userIdClaim.Value;
        return Guid.Parse(userId);
    }
}