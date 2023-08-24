namespace PasswordManager.Tests.Services;


public class UserServiceTests
{
    private readonly string _email;

    private readonly User _inactiveUser;
    private readonly User _activeUser;

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
    }
    
    [Fact]
    public async void RegisterAsync_ThrowsEmailAlreadyExistsException()
    {
        var userRepositoryMock = new Mock<IUserRepository>();
        userRepositoryMock.Setup(u => u.CheckCommunicationAddressExistsAsync(_email)).ReturnsAsync(true);
        
        var sut = new UserService(userRepositoryMock.Object);
        await Assert.ThrowsAsync<EmailAlreadyExistsException>(() => sut.RegisterAsync(_inactiveUser));
    }   

    [Fact]
    public async void RegisterAsync_Successful()
    {
        var userRepositoryMock = new Mock<IUserRepository>();

        userRepositoryMock.Setup(u => u.CheckCommunicationAddressExistsAsync(_email)).ReturnsAsync(false);
        userRepositoryMock.Setup(u => u.SaveAsync(_inactiveUser)).ReturnsAsync(_inactiveUser);

        var sut = new UserService(userRepositoryMock.Object);
        var result = await sut.RegisterAsync(_inactiveUser);
        
        Assert.Equivalent(_inactiveUser, result);
        userRepositoryMock.Verify(u => u.CheckCommunicationAddressExistsAsync(It.IsAny<string>()), Times.Once);
        userRepositoryMock.Verify(u => u.SaveAsync(It.IsAny<User>()), Times.Once);
    }

    [Fact]
    public async void GetUserAsync_ByCommunicationAddress_ThrowsEmailOrPasswordIsIncorrectException()
    {
        var userRepositoryMock = new Mock<IUserRepository>();

        userRepositoryMock.Setup(u => u.GetAsync(_inactiveUser.CommunicationAddress)).ReturnsAsync((User) null);

        var sut = new UserService(userRepositoryMock.Object);
        
        await Assert.ThrowsAsync<EmailOrPasswordIsIncorrectException>(() => sut.GetUserAsync(_inactiveUser.CommunicationAddress));
        userRepositoryMock.Verify(u => u.GetAsync(It.IsAny<string>()), Times.Once);
    }
    
    [Fact]
    public async void GetUserAsync_ByCommunicationAddress_ThrowsAccountNotActivatedException()
    {
        var userRepositoryMock = new Mock<IUserRepository>();

        userRepositoryMock.Setup(u => u.GetAsync(_inactiveUser.CommunicationAddress)).ReturnsAsync(_inactiveUser);

        var sut = new UserService(userRepositoryMock.Object);
        
        await Assert.ThrowsAsync<AccountNotActivatedException>(() => sut.GetUserAsync(_inactiveUser.CommunicationAddress));
        userRepositoryMock.Verify(u => u.GetAsync(It.IsAny<string>()), Times.Once);
    }
    
    [Fact]
    public async void GetUserAsync_ByCommunicationAddress_Successful()
    {
        var userRepositoryMock = new Mock<IUserRepository>();

        userRepositoryMock.Setup(u => u.GetAsync(_activeUser.CommunicationAddress)).ReturnsAsync(_activeUser);

        var sut = new UserService(userRepositoryMock.Object);
        var result = await sut.GetUserAsync(_activeUser.CommunicationAddress);
        
        Assert.Equivalent(_activeUser, result);
        userRepositoryMock.Verify(u => u.GetAsync(It.IsAny<string>()), Times.Once);
    }

    [Fact]
    public async void GetUserAsync_ById_ThrowsUserNotFoundException()
    {
        var userRepositoryMock = new Mock<IUserRepository>();

        userRepositoryMock.Setup(u => u.GetAsync(_inactiveUser.Id)).ReturnsAsync((User) null);

        var sut = new UserService(userRepositoryMock.Object);
        
        await Assert.ThrowsAsync<UserNotFoundException>(() => sut.GetUserAsync(_inactiveUser.Id));
        userRepositoryMock.Verify(u => u.GetAsync(It.IsAny<Guid>()), Times.Once);
    }
    
    [Fact]
    public async void GetUserAsync_ById_ThrowsAccountNotActivatedException()
    {
        var userRepositoryMock = new Mock<IUserRepository>();

        userRepositoryMock.Setup(u => u.GetAsync(_inactiveUser.Id)).ReturnsAsync(_inactiveUser);

        var sut = new UserService(userRepositoryMock.Object);
        
        await Assert.ThrowsAsync<AccountNotActivatedException>(() => sut.GetUserAsync(_inactiveUser.Id));
        userRepositoryMock.Verify(u => u.GetAsync(It.IsAny<Guid>()), Times.Once);
    }
    
    [Fact]
    public async void GetUserAsync_ById_Successful()
    {
        var userRepositoryMock = new Mock<IUserRepository>();

        userRepositoryMock.Setup(u => u.GetAsync(_activeUser.Id)).ReturnsAsync(_activeUser);

        var sut = new UserService(userRepositoryMock.Object);
        var result = await sut.GetUserAsync(_activeUser.Id);
        
        Assert.Equivalent(_activeUser, result);
        userRepositoryMock.Verify(u => u.GetAsync(It.IsAny<Guid>()), Times.Once);
    }
}