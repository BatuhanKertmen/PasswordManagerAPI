namespace PasswordManager.Tests.Services;

public class JwtServiceTests
{
    private readonly Mock<ISecretManager> _secretManagerMock;
    private readonly User _user;
    private readonly JwtInfo _jwtInfo;
    private readonly byte[] _jwtKey;
    private readonly string _jwtToken;

    public JwtServiceTests()
    {
        _secretManagerMock = new Mock<ISecretManager>();

        _user = new User
        {
            Id = Guid.Parse("4a42cc1d-8a42-4ca8-99f5-5c82dba9db36"),
            CommunicationAddress = "test@example.com",
            Active = false
        };

        _jwtInfo = new JwtInfo
        {
            Audience = "test",
            Issuer = "Test"
        };

        _jwtKey = Encoding.UTF8.GetBytes("private-key-signing-jwt");

        _jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3ByaW1hcnlzaWQiOiI0YTQyY2MxZC04YTQyLTRjYTgtOTlmNS01YzgyZGJhOWRiMzYiLCJleHAiOjE2OTMxMzMwODQsImlzcyI6IlRlc3QiLCJhdWQiOiJ0ZXN0In0.mCuK0LbOev824MyHiJg7Q9VHsZoY0xpapk905of4QXI";
    }

    [Fact]
    public void CreateJwtTokenAsync_ThrowsSecretNotAvailableException()
    {
        _secretManagerMock.Setup(s => s.GetJwtInfo()).Returns((JwtInfo?)null);
        _secretManagerMock.Setup(s => s.GetJwtKey()).Returns((byte[]?)null);

        var sut = new JwtService(_secretManagerMock.Object);

        Assert.Throws<SecretNotAvailableException>(() => sut.CreateJwtToken(_user));
        _secretManagerMock.Verify(s => s.GetJwtKey(), Times.Once);
        _secretManagerMock.Verify(s => s.GetJwtInfo(), Times.Once);
    }
    
    [Fact]
    public void CreateJwtTokenAsync_ReturnsJwtToken()
    {
        _secretManagerMock.Setup(s => s.GetJwtInfo()).Returns(_jwtInfo);
        _secretManagerMock.Setup(s => s.GetJwtKey()).Returns(_jwtKey);

        var sut = new JwtService(_secretManagerMock.Object);
        var result = sut.CreateJwtToken(_user);
        
        var handler = new JwtSecurityTokenHandler();
        var jwtSecurityToken = handler.ReadJwtToken(result);
        var userIdClaim = jwtSecurityToken.Claims.First(claim => claim.Type == ClaimTypes.PrimarySid);
        var issuerClaim = jwtSecurityToken.Claims.First(claim => claim.Type == "iss");
        var audienceClaim = jwtSecurityToken.Claims.First(claim => claim.Type == "aud");
        
        Assert.Equal(_user.Id.ToString(), userIdClaim.Value);
        Assert.Equal(_jwtInfo.Issuer, issuerClaim.Value);
        Assert.Equal(_jwtInfo.Audience, audienceClaim.Value);
        _secretManagerMock.Verify(s => s.GetJwtKey(), Times.Once);
        _secretManagerMock.Verify(s => s.GetJwtInfo(), Times.Once);
    }

    [Fact]
    public void GetUserId_ReturnsUserId()
    {
        var sut = new JwtService(_secretManagerMock.Object);
        var result = sut.GetUserId("Bearer " + _jwtToken);
        
        Assert.Equal(_user.Id, result);
    }
}