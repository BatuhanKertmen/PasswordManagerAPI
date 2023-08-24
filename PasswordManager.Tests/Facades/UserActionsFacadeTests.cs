

namespace PasswordManager.Tests.Facades;

public class UserActionsFacadeTests
{
    private readonly Mock<IMapper> _mapperMock;
    private readonly Mock<IUserService> _userServiceMock;
    private readonly Mock<IUserPasswordService> _userPasswordServiceMock;
    private readonly Mock<IActivationCodeService> _activationCodeServiceMock;
    private readonly Mock<IJwtService> _jwtServiceMock;
    
    private readonly UserRegisterRequestDto _userRegisterRequest;
    private readonly User _registeredUser;
    private readonly UserRegisterResponseDto _registeredUserResponseDto;
    private readonly UserLoginResponseDto _userLoginResponseDto;
    private readonly UserLoginRequestDto _userLoginRequest;

    public UserActionsFacadeTests()
    {
        _mapperMock  = new Mock<IMapper>();
        _userServiceMock = new Mock<IUserService>();
        _userPasswordServiceMock = new Mock<IUserPasswordService>();
        _activationCodeServiceMock = new Mock<IActivationCodeService>();
        _jwtServiceMock  = new Mock<IJwtService>();

        _userRegisterRequest = new UserRegisterRequestDto();
        _registeredUser = new User();
        _registeredUserResponseDto = new UserRegisterResponseDto();
        _userLoginResponseDto = new UserLoginResponseDto();
        _userLoginRequest = new UserLoginRequestDto();
    }

    [Fact]
    public async Task RegisterAsync_SuccessfulRegistration_ReturnsUserRegisterResponseDto()
    {
        _userServiceMock.Setup(u => u.RegisterAsync(It.IsAny<User>())).ReturnsAsync(_registeredUser);
        _mapperMock.Setup(m => m.Map<User>(_userRegisterRequest)).Returns(new User());
        _mapperMock.Setup(m => m.Map<UserRegisterResponseDto>(_registeredUser)).Returns(_registeredUserResponseDto);

        var sut = new UserActionsFacade(
            _userServiceMock.Object,
            _userPasswordServiceMock.Object,
            _activationCodeServiceMock.Object,
            _mapperMock.Object,
            _jwtServiceMock.Object);
        
        var result = await sut.RegisterAsync(_userRegisterRequest);

        Assert.Equal(_registeredUserResponseDto, result);
        _userServiceMock.Verify(u => u.RegisterAsync(It.IsAny<User>()), Times.Once);
        _userPasswordServiceMock.Verify(u => u.SaveAsync(It.IsAny<string>(), It.IsAny<UserPassword>(), It.IsAny<User>()), Times.Once);
        _activationCodeServiceMock.Verify(a => a.SendActivationCode(It.IsAny<User>()), Times.Once);
        _mapperMock.Verify(m => m.Map<User>(_userRegisterRequest), Times.Once);
        _mapperMock.Verify(m => m.Map<UserRegisterResponseDto>(_registeredUser), Times.Once);
    }
    
    [Fact]
    public async Task ActivateAccountAsync_ValidToken_ReturnsTrue()
    {
        _activationCodeServiceMock.Setup(a => a.ActivateAccountAsync(It.IsAny<Guid>(), It.IsAny<string>())).ReturnsAsync(true);

        var sut = new UserActionsFacade(
            _userServiceMock.Object,
            _userPasswordServiceMock.Object,
            _activationCodeServiceMock.Object,
            _mapperMock.Object,
            _jwtServiceMock.Object);

        // Act
        var result = await sut.ActivateAccountAsync(Guid.NewGuid(), "valid-token");

        // Assert
        Assert.True(result);
        _activationCodeServiceMock.Verify(a => a.ActivateAccountAsync(It.IsAny<Guid>(), It.IsAny<string>()), Times.Once);
    }
    
    [Fact]
    public async Task LoginAsync_IncorrectPassword_ThrowsEmailOrPasswordIsIncorrectException()
    {
        _userPasswordServiceMock.Setup(u => u.CheckPassword(It.IsAny<Guid>(), It.IsAny<string>())).ReturnsAsync(false);
        _userServiceMock.Setup(u => u.GetUserAsync(It.IsAny<string>())).ReturnsAsync(_registeredUser);
        
        var sut = new UserActionsFacade(
            _userServiceMock.Object,
            _userPasswordServiceMock.Object,
            _activationCodeServiceMock.Object,
            _mapperMock.Object,
            _jwtServiceMock.Object);
        
        await Assert.ThrowsAsync<EmailOrPasswordIsIncorrectException>(() =>
            sut.LoginAsync(new UserLoginRequestDto()));
    }
    
    [Fact]
    public async Task LoginAsync_CorrectPassword_ReturnsUserLoginResponseDto()
    {
        var jwtToken = "valid-jwt-token";

        _userServiceMock.Setup(u => u.GetUserAsync(It.IsAny<string>())).ReturnsAsync(_registeredUser);
        _userPasswordServiceMock.Setup(u => u.CheckPassword(It.IsAny<Guid>(), It.IsAny<string>())).ReturnsAsync(true);
        _jwtServiceMock.Setup(j => j.CreateJwtToken(It.IsAny<User>())).Returns(jwtToken);
        _mapperMock.Setup(m => m.Map<UserLoginResponseDto>(jwtToken)).Returns(_userLoginResponseDto);

        var sut = new UserActionsFacade(
            _userServiceMock.Object,
            _userPasswordServiceMock.Object,
            _activationCodeServiceMock.Object,
            _mapperMock.Object,
            _jwtServiceMock.Object);
        
        var result = await sut.LoginAsync(_userLoginRequest);

        // Assert
        Assert.Equal(_userLoginResponseDto, result);
        _userServiceMock.Verify(u => u.GetUserAsync(It.IsAny<string>()), Times.Once);
        _userPasswordServiceMock.Verify(u => u.CheckPassword(It.IsAny<Guid>(), It.IsAny<string>()), Times.Once);
        _jwtServiceMock.Verify(j => j.CreateJwtToken(It.IsAny<User>()), Times.Once);
        _mapperMock.Verify(m => m.Map<UserLoginResponseDto>(jwtToken), Times.Once);
    }

}