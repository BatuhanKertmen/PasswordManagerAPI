namespace PasswordManager.Tests.Services;

public class LoginInformationServiceTests
{
    private readonly Mock<ILoginInformationRepository> _repository;
    private readonly LoginInformation _loginInfo;

    public LoginInformationServiceTests()
    {
        _repository = new Mock<ILoginInformationRepository>();
        _loginInfo = new LoginInformation
        {
            Domain = "test.com",
            Id = Guid.NewGuid(),
            PasswordEncrypted = "encrypted-password",
            UsernameEncrypted = "encrypted-username"
        };
    }

    [Fact]
    public async Task SaveAsync_ReturnsLoginInformation()
    {
        _repository.Setup(r => r.SaveAsync(_loginInfo)).ReturnsAsync(_loginInfo);

        var sut = new LoginInformationService(_repository.Object);
        var result = await sut.SaveAsync(_loginInfo);
        
        Assert.Equal(_loginInfo, result);
        _repository.Verify(r => r.SaveAsync(_loginInfo), Times.Once);
    }

    [Fact]
    public async Task GelAllByDomainAsync_ReturnsLoginInformationEnumerable()
    {
        _repository.Setup(r => r.GetAllByDomainAsync(_loginInfo.Domain))
            .ReturnsAsync(new List<LoginInformation> { _loginInfo });
        
        var sut = new LoginInformationService(_repository.Object);
        var result = await sut.GelAllByDomainAsync(_loginInfo.Domain);
        var resultList = result.ToList();

        Assert.Single(resultList);
        Assert.Equivalent(_loginInfo, resultList.First());
        _repository.Verify(r => r.GetAllByDomainAsync(_loginInfo.Domain), Times.Once);
    }
}