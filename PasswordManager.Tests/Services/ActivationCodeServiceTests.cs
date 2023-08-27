namespace PasswordManager.Tests.Services;

public class ActivationCodeServiceTests
{
    private readonly Mock<IActivationCodeRepository> _activationCodeRepositoryMock = new Mock<IActivationCodeRepository>();
    private readonly Mock<IUserRepository> _userRepositoryMock = new Mock<IUserRepository>();
    private readonly Mock<ICommunicationChannel> _communicationChannelMock = new Mock<ICommunicationChannel>();
    private readonly Mock<ISecretManager> _secretManagerMock = new Mock<ISecretManager>();
    private readonly Mock<IHttpContextAccessor> _httpContextAccessorMock = new Mock<IHttpContextAccessor>();
    private readonly Mock<LinkGenerator> _linkGeneratorMock = new Mock<LinkGenerator>();

    private readonly User _inactiveUser;

    private readonly ActivationCode _invalidCode;
    private readonly ActivationCode _validCode;

    private readonly string _testHmacKey;
    private readonly string _validCodeHashHexString;
    private readonly string _invalidCodeHashHexString;

    public ActivationCodeServiceTests()
    {
        _inactiveUser = new User
        {
            Id = Guid.Parse("4a42cc1d-8a42-4ca8-99f5-5c82dba9db36"),
            CommunicationAddress = "test@example.com",
            Active = false
        };
        
        _testHmacKey = "abcd";
        _validCodeHashHexString = "94F2F3F3D6D4CF1AD0066064C1B6AC005C2008058E01368ADA3888019D98DF4D";
        _invalidCodeHashHexString = "invalid";
        
        _invalidCode = new ActivationCode
        {
            ExpiryDate = DateTime.UtcNow.AddDays(1),
            Code = "invalid-code",
            User = _inactiveUser
        };

        _validCode = new ActivationCode
        {
            ExpiryDate = DateTime.UtcNow.AddDays(1),
            Code = "valid-code",
            User = _inactiveUser
        };
    }
    
    [Fact]
    public async Task ActivateAccountAsync_ValidToken_ReturnsTrue()
    {
        _activationCodeRepositoryMock.Setup(a => a.GetActivationCodesByUser_IdAsync(_inactiveUser.Id))
                                   .ReturnsAsync(new List<ActivationCode>{ _invalidCode, _validCode });
        _userRepositoryMock.Setup(u => u.ActivateAccountAsync(_inactiveUser.Id))
                          .ReturnsAsync(new User());
        _secretManagerMock.Setup(s => s.GetHmacPrivateKey()).Returns(Convert.FromHexString(_testHmacKey));

        var sut = new ActivationCodeService(
            _activationCodeRepositoryMock.Object,
            _secretManagerMock.Object,
            _communicationChannelMock.Object,
            _userRepositoryMock.Object,
            _httpContextAccessorMock.Object,
            _linkGeneratorMock.Object);
        
        var result = await sut.ActivateAccountAsync(_inactiveUser.Id, _validCodeHashHexString);

        Assert.True(result);
        _activationCodeRepositoryMock.Verify(a => a.GetActivationCodesByUser_IdAsync(_inactiveUser.Id), Times.Once);
        _userRepositoryMock.Verify(u => u.ActivateAccountAsync(_inactiveUser.Id), Times.Once);
    }
    
    [Fact]
    public async Task ActivateAccountAsync_InvalidToken_ReturnsFalse()
    {
        _activationCodeRepositoryMock.Setup(a => a.GetActivationCodesByUser_IdAsync(_inactiveUser.Id))
            .ReturnsAsync(new List<ActivationCode>{ _invalidCode, _validCode });
        _userRepositoryMock.Setup(u => u.ActivateAccountAsync(_inactiveUser.Id))
            .ReturnsAsync(new User());
        _secretManagerMock.Setup(s => s.GetHmacPrivateKey()).Returns(Convert.FromHexString(_testHmacKey));

        var sut = new ActivationCodeService(
            _activationCodeRepositoryMock.Object,
            _secretManagerMock.Object,
            _communicationChannelMock.Object,
            _userRepositoryMock.Object,
            _httpContextAccessorMock.Object,
            _linkGeneratorMock.Object);
        
        var result = await sut.ActivateAccountAsync(_inactiveUser.Id, _invalidCodeHashHexString);

        Assert.False(result);
        _activationCodeRepositoryMock.Verify(a => a.GetActivationCodesByUser_IdAsync(_inactiveUser.Id), Times.Once);
        _userRepositoryMock.Verify(u => u.ActivateAccountAsync(_inactiveUser.Id), Times.Never);
    }
    
    [Fact]
    public async Task ActivateAccountAsync_CanNotReachKey_ThrowsSecretNotAvailableException()
    {
        _activationCodeRepositoryMock.Setup(a => a.GetActivationCodesByUser_IdAsync(_inactiveUser.Id))
            .ReturnsAsync(new List<ActivationCode>{ _invalidCode, _validCode });
        _userRepositoryMock.Setup(u => u.ActivateAccountAsync(_inactiveUser.Id))
            .ReturnsAsync(new User());
        _secretManagerMock.Setup(s => s.GetHmacPrivateKey()).Returns((byte[]?)null);

        var sut = new ActivationCodeService(
            _activationCodeRepositoryMock.Object,
            _secretManagerMock.Object,
            _communicationChannelMock.Object,
            _userRepositoryMock.Object,
            _httpContextAccessorMock.Object,
            _linkGeneratorMock.Object);

        await Assert.ThrowsAsync<SecretNotAvailableException>(() => sut.ActivateAccountAsync(_inactiveUser.Id, _invalidCodeHashHexString));
        _activationCodeRepositoryMock.Verify(a => a.GetActivationCodesByUser_IdAsync(_inactiveUser.Id), Times.Once);
        _userRepositoryMock.Verify(u => u.ActivateAccountAsync(_inactiveUser.Id), Times.Never);
    }
    
    
}