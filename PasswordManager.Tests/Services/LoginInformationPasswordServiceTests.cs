namespace PasswordManager.Tests.Services;

public class LoginInformationPasswordServiceTests
{
    private readonly LoginInformationPassword _loginInformationPassword;
    private readonly Mock<ILoginInformationPasswordRepository> _loginInformationPasswordRepositoryMock;
    
    public LoginInformationPasswordServiceTests()
    {

        _loginInformationPassword = new LoginInformationPassword
        {
            Id = Guid.NewGuid(),
            DegreeOfParallelism = 1,
            Iterations = 1,
            MemorySize = 1024,
            Salt = Convert.FromHexString("abcd")
        };

        _loginInformationPasswordRepositoryMock = new Mock<ILoginInformationPasswordRepository>();
    }
    
    [Fact]
    public async Task SaveAsync_ValidLoginInformationPassword_SavesSuccessfully()
    {
        
        _loginInformationPasswordRepositoryMock.Setup(repo => repo.SaveAsync(_loginInformationPassword))
            .ReturnsAsync(_loginInformationPassword);

        var sut = new LoginInformationPasswordService(_loginInformationPasswordRepositoryMock.Object);
        
        var result = await sut.SaveAsync(_loginInformationPassword);
        
        Assert.NotNull(result);
        Assert.Equivalent(_loginInformationPassword, result);
        
        _loginInformationPasswordRepositoryMock.Verify(repo => repo.SaveAsync(_loginInformationPassword), Times.Once);
    }
}