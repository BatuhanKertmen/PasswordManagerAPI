namespace PasswordManager.Tests.Services;

public class UserPasswordServiceTests
{
    private readonly Mock<ISecretManager> _secretManager;
    private readonly Mock<IUserPasswordRepository> _repository;
    private readonly UserPassword _userPassword;
    private readonly UserPassword _recordedUserPassword;
    private readonly UserPassword _recordedUserPasswordFalseTag;
    private readonly UserPassword _recordedUserPasswordFalsePassword;
    
    private readonly byte[] _aesTestKey;
    private readonly string _passwordHexString;
    private readonly User _user;

    public UserPasswordServiceTests()
    {
        _secretManager = new Mock<ISecretManager>();
        _repository = new Mock<IUserPasswordRepository>();

        _userPassword = new UserPassword
        {
            DegreeOfParallelism = 1,
            Iterations = 1,
            MemorySize = 1024,
            Salt = Convert.FromHexString("abcd")
        };

        _recordedUserPassword = new UserPassword
        {
            DegreeOfParallelism = 1,
            Iterations = 2,
            MemorySize = 19 * 1024,
            Salt = Convert.FromHexString("82db97649c8436f9e67a3a91c1af31afa128db08aee5ec79b7a58db42033d69b"),
            Id = Guid.NewGuid(),
            Nonce = Convert.FromHexString("bea665b50fbbf91038e0de9c"),
            Tag = Convert.FromHexString("2A3C07D136B71CC2FBE1D3FB31176D8B"),
            Password = Convert.FromHexString("4918C9710F50")
        };
        
        _recordedUserPasswordFalseTag = new UserPassword
        {
            DegreeOfParallelism = 1,
            Iterations = 2,
            MemorySize = 19 * 1024,
            Salt = Convert.FromHexString("82db97649c8436f9e67a3a91c1af31afa128db08aee5ec79b7a58db42033d69b"),
            Id = Guid.NewGuid(),
            Nonce = Convert.FromHexString("bea665b50fbbf91038e0de9c"),
            Tag = Encoding.UTF8.GetBytes("false-tag"),
            Password = Convert.FromHexString("4918C9710F50")
        };
        
        _recordedUserPasswordFalsePassword = new UserPassword
        {
            DegreeOfParallelism = 1,
            Iterations = 2,
            MemorySize = 19 * 1024,
            Salt = Convert.FromHexString("82db97649c8436f9e67a3a91c1af31afa128db08aee5ec79b7a58db42033d69b"),
            Id = Guid.NewGuid(),
            Nonce = Convert.FromHexString("bea665b50fbbf91038e0de9c"),
            Tag = Convert.FromHexString("2A3C07D136B71CC2FBE1D3FB31176D8B"),
            Password = Encoding.UTF8.GetBytes("false-password")
        };

        _aesTestKey = Convert.FromHexString("581f4cb1ccfc2f718fa7d547bbe9939dbe46dfbd2d50ec7b98c17dc04ef59322");
        _passwordHexString = "9f3d7e2ca5f0";

        _user = new User
        {
            CommunicationAddress = "test@example.com",
            Active = true,
            Id = Guid.NewGuid()
        };
    }
    
    [Fact]
    public async Task SaveAsync_ThrowsSecretNotAvailableException()
    {
        _repository.Setup(r => r.SaveAsync(_userPassword)).ReturnsAsync(_userPassword);
        _secretManager.Setup(s => s.GetPepperSymmetricKey()).Returns((byte[]?) null);

        var sut = new UserPasswordService(_repository.Object, _secretManager.Object);

        await Assert.ThrowsAsync<SecretNotAvailableException>(() => sut.SaveAsync(_passwordHexString, _userPassword, _user));
        _secretManager.Verify(s => s.GetPepperSymmetricKey(), Times.Once());
        _repository.Verify(r => r.SaveAsync(_userPassword), Times.Never);
    }

    [Fact]
    public async Task SaveAsync_ReturnsUserPassword()
    {
        _repository.Setup(r => r.SaveAsync(_userPassword)).ReturnsAsync(_userPassword);
        _secretManager.Setup(s => s.GetPepperSymmetricKey()).Returns(_aesTestKey);

        var sut = new UserPasswordService(_repository.Object, _secretManager.Object);
        var result = await sut.SaveAsync(_passwordHexString, _userPassword, _user);
        
        Assert.Equal(_user.Id, result.UserId);
        Assert.Equal(_userPassword.Iterations, result.Iterations);
        Assert.Equal(_userPassword.Salt, result.Salt);
        Assert.Equal(_userPassword.DegreeOfParallelism, result.DegreeOfParallelism);
        Assert.Equal(_userPassword.MemorySize, result.MemorySize);
        _repository.Verify(r => r.SaveAsync(_userPassword), Times.Once);
        _secretManager.Verify(s => s.GetPepperSymmetricKey(), Times.Once);
    }

    [Fact]
    public async Task CheckPassword_ThrowsUserPasswordNotFoundException()
    {
        _repository.Setup(r => r.GetAsync(_user.Id)).ReturnsAsync((UserPassword?)null);
        
        var sut = new UserPasswordService(_repository.Object, _secretManager.Object);

        await Assert.ThrowsAsync<UserPasswordNotFoundException>(() => sut.CheckPasswordAsync(_user.Id, _passwordHexString));
        _repository.Verify(r => r.GetAsync(_user.Id), Times.Once);
    }
    
    [Fact]
    public async Task CheckPassword_FalseTag_ReturnsFalse()
    {
        _repository.Setup(r => r.GetAsync(_user.Id)).ReturnsAsync(_recordedUserPasswordFalseTag);
        _secretManager.Setup(s => s.GetPepperSymmetricKey()).Returns(_aesTestKey);
        
        var sut = new UserPasswordService(_repository.Object, _secretManager.Object);
        var result = await sut.CheckPasswordAsync(_user.Id, _passwordHexString);
        
        Assert.False(result);
        _repository.Verify(r => r.GetAsync(_user.Id), Times.Once);
        _secretManager.Verify(s => s.GetPepperSymmetricKey(), Times.Once);
    }
    
    [Fact]
    public async Task CheckPassword_FalsePassword_ReturnsFalse()
    {
        _repository.Setup(r => r.GetAsync(_user.Id)).ReturnsAsync(_recordedUserPasswordFalsePassword);
        _secretManager.Setup(s => s.GetPepperSymmetricKey()).Returns(_aesTestKey);
        
        var sut = new UserPasswordService(_repository.Object, _secretManager.Object);
        var result = await sut.CheckPasswordAsync(_user.Id, _passwordHexString);
        
        Assert.False(result);
        _repository.Verify(r => r.GetAsync(_user.Id), Times.Once);
        _secretManager.Verify(s => s.GetPepperSymmetricKey(), Times.Once);
    }

    [Fact]
    public async Task CheckPassword_ReturnsTrue()
    {
        _repository.Setup(r => r.GetAsync(_user.Id)).ReturnsAsync(_recordedUserPassword);
        _secretManager.Setup(s => s.GetPepperSymmetricKey()).Returns(_aesTestKey);

        var sut = new UserPasswordService(_repository.Object, _secretManager.Object);
        var result = await sut.CheckPasswordAsync(_user.Id, _passwordHexString);

        Assert.True(result);
        _repository.Verify(r => r.GetAsync(_user.Id), Times.Once);
        _secretManager.Verify(s => s.GetPepperSymmetricKey(), Times.Once);
    }
}