namespace PasswordManager.Tests.Services;

public class UserServiceTests
{
    private readonly string _email;

    private readonly User _inactiveUser;
    private readonly User _activeUser;
    private readonly Mock<IUserRepository> _repository;

    public UserServiceTests()
    {
        _email = "test@example.com";

        _inactiveUser = new User
        {
            CommunicationAddress = _email,
            Active = false,
            Id = new Guid()
        };

        _activeUser = new User() {
            CommunicationAddress = _email,
            Active = true,
            Id = new Guid()
        };

        _repository = new Mock<IUserRepository>();
    }
    
    [Fact]
    public async Task RegisterAsync_ThrowsEmailAlreadyExistsException()
    {
        _repository.Setup(u => u.CheckCommunicationAddressExistsAsync(_email)).ReturnsAsync(true);
        
        var sut = new UserService(_repository.Object);
        await Assert.ThrowsAsync<EmailAlreadyExistsException>(() => sut.RegisterAsync(_inactiveUser));
    }   

    [Fact]
    public async Task RegisterAsync_Successful()
    {
        _repository.Setup(u => u.CheckCommunicationAddressExistsAsync(_email)).ReturnsAsync(false);
        _repository.Setup(u => u.SaveAsync(_inactiveUser)).ReturnsAsync(_inactiveUser);

        var sut = new UserService(_repository.Object);
        var result = await sut.RegisterAsync(_inactiveUser);
        
        Assert.Equivalent(_inactiveUser, result);
        _repository.Verify(u => u.CheckCommunicationAddressExistsAsync(It.IsAny<string>()), Times.Once);
        _repository.Verify(u => u.SaveAsync(It.IsAny<User>()), Times.Once);
    }

    [Fact]
    public async Task GetUserAsync_ByCommunicationAddress_ThrowsEmailOrPasswordIsIncorrectException()
    {
        _repository.Setup(u => u.GetAsync(_inactiveUser.CommunicationAddress)).ReturnsAsync((User) null);

        var sut = new UserService(_repository.Object);
        
        await Assert.ThrowsAsync<EmailOrPasswordIsIncorrectException>(() => sut.GetUserAsync(_inactiveUser.CommunicationAddress));
        _repository.Verify(u => u.GetAsync(It.IsAny<string>()), Times.Once);
    }
    
    [Fact]
    public async Task GetUserAsync_ByCommunicationAddress_ThrowsAccountNotActivatedException()
    {
        _repository.Setup(u => u.GetAsync(_inactiveUser.CommunicationAddress)).ReturnsAsync(_inactiveUser);

        var sut = new UserService(_repository.Object);
        
        await Assert.ThrowsAsync<AccountNotActivatedException>(() => sut.GetUserAsync(_inactiveUser.CommunicationAddress));
        _repository.Verify(u => u.GetAsync(It.IsAny<string>()), Times.Once);
    }
    
    [Fact]
    public async Task GetUserAsync_ByCommunicationAddress_Successful()
    {
        _repository.Setup(u => u.GetAsync(_activeUser.CommunicationAddress)).ReturnsAsync(_activeUser);

        var sut = new UserService(_repository.Object);
        var result = await sut.GetUserAsync(_activeUser.CommunicationAddress);
        
        Assert.Equivalent(_activeUser, result);
        _repository.Verify(u => u.GetAsync(It.IsAny<string>()), Times.Once);
    }

    [Fact]
    public async Task GetUserAsync_ById_ThrowsUserNotFoundException()
    {
        _repository.Setup(u => u.GetAsync(_inactiveUser.Id)).ReturnsAsync((User) null);

        var sut = new UserService(_repository.Object);
        
        await Assert.ThrowsAsync<UserNotFoundException>(() => sut.GetUserAsync(_inactiveUser.Id));
        _repository.Verify(u => u.GetAsync(It.IsAny<Guid>()), Times.Once);
    }
    
    [Fact]
    public async Task GetUserAsync_ById_ThrowsAccountNotActivatedException()
    {
        _repository.Setup(u => u.GetAsync(_inactiveUser.Id)).ReturnsAsync(_inactiveUser);

        var sut = new UserService(_repository.Object);
        
        await Assert.ThrowsAsync<AccountNotActivatedException>(() => sut.GetUserAsync(_inactiveUser.Id));
        _repository.Verify(u => u.GetAsync(It.IsAny<Guid>()), Times.Once);
    }
    
    [Fact]
    public async Task GetUserAsync_ById_Successful()
    {
        _repository.Setup(u => u.GetAsync(_activeUser.Id)).ReturnsAsync(_activeUser);

        var sut = new UserService(_repository.Object);
        var result = await sut.GetUserAsync(_activeUser.Id);
        
        Assert.Equivalent(_activeUser, result);
        _repository.Verify(u => u.GetAsync(It.IsAny<Guid>()), Times.Once);
    }
}