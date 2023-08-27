namespace PasswordManager.Tests.Repositories;

public class UserRepositoryTests
{
    private readonly ISecretManager _secretManager;
    private readonly DbContextOptions<AppDbContext> _options;
    private readonly User _user;
    private readonly User _inactiveUser;
    private readonly User _activeuser;
    private readonly string _nonExistingEmail;
    
    
    public UserRepositoryTests()
    {
        _secretManager = new Mock<ISecretManager>().Object;
        
        _options = new DbContextOptionsBuilder<AppDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;

        _user = new User
        {
            Id = new Guid(),
            CommunicationAddress = "test@example.com"
        };

        _inactiveUser = new User
        {
            Id = new Guid(),
            CommunicationAddress = "inactive@example.com",
            Active = false
        };

        _activeuser = new User
        {
            Id = new Guid(),
            CommunicationAddress = "active@example.com",
            Active = true
        };

        _nonExistingEmail = "no-user@example.com";
    }

    [Fact]
    public async Task CheckCommunicationAddressExistsAsync_EmailExists_ReturnsTrue()
    {
        using (var context = new AppDbContext(_options, _secretManager))
        {
            await context.Users.AddAsync(_user);
            await context.SaveChangesAsync();
        }

        using (var context = new AppDbContext(_options, _secretManager))
        {
            var userRepository = new UserRepository(context);
            
            var exists = await userRepository.CheckCommunicationAddressExistsAsync(_user.CommunicationAddress);
            
            Assert.True(exists);
        }
    }

    [Fact]
    public async Task CheckCommunicationAddressExistsAsync_EmailDoesNotExist_ReturnsFalse()
    {
        using (var context = new AppDbContext(_options, _secretManager))
        {
            var userRepository = new UserRepository(context);

            var exists = await userRepository.CheckCommunicationAddressExistsAsync(_nonExistingEmail);

            Assert.False(exists);
        }
    }
    
    [Fact]
    public async Task SaveAsync_ValidUser_ReturnsSavedUser()
    {
        using (var context = new AppDbContext(_options, _secretManager))
        {
            var sut = new UserRepository(context);
            
            var savedUser = await sut.SaveAsync(_user);
            
            Assert.NotNull(savedUser);
            Assert.Equal(_user.CommunicationAddress, savedUser.CommunicationAddress);
        }
    }
    
    [Fact]
    public async Task ActivateAccountAsync_InactiveUser_ReturnsActivatedUser()
    {
        using (var context = new AppDbContext(_options, _secretManager))
        {
            await context.Users.AddAsync(_inactiveUser);
            await context.SaveChangesAsync();
        }

        using (var context = new AppDbContext(_options, _secretManager))
        {
            var sut = new UserRepository(context);

            // Act
            var activatedUser = await sut.ActivateAccountAsync(_inactiveUser.Id);

            // Assert
            Assert.NotNull(activatedUser);
            Assert.True(activatedUser.Active);
        }
    }
    
    [Fact]
    public async Task ActivateAccountAsync_ActiveUser_ReturnsNull()
    {
        using (var context = new AppDbContext(_options, _secretManager))
        {
            await context.Users.AddAsync(_activeuser);
            await context.SaveChangesAsync();
        }

        using (var context = new AppDbContext(_options, _secretManager))
        {
            var sut = new UserRepository(context);
            
            var activatedUser = await sut.ActivateAccountAsync(_activeuser.Id);
            
            Assert.Null(activatedUser);
        }
    }
    
    [Fact]
    public async Task GetAsync_ByAddress_UserExists_ReturnsUser()
    {
        using (var context = new AppDbContext(_options, _secretManager))
        {
            await context.Users.AddAsync(_user);
            await context.SaveChangesAsync();
        }

        using (var context = new AppDbContext(_options, _secretManager))
        {
            var sut = new UserRepository(context);
            
            var result = await sut.GetAsync(_user.CommunicationAddress);
            
            Assert.NotNull(result);
            Assert.Equal(_user.CommunicationAddress, result.CommunicationAddress);
        }
    }
    
    [Fact]
    public async Task GetAsync_ByAddress_UserDoesNotExist_ReturnsNull()
    {
        using (var context = new AppDbContext(_options, _secretManager))
        {
            var sut = new UserRepository(context);
            
            var user = await sut.GetAsync(_nonExistingEmail);
            
            Assert.Null(user);
        }
    }
    
    [Fact]
    public async Task GetAsync_ById_UserExists_ReturnsUser()
    {
        using (var context = new AppDbContext(_options, _secretManager))
        {
            await context.Users.AddAsync(_user);
            await context.SaveChangesAsync();
        }

        using (var context = new AppDbContext(_options, _secretManager))
        {
            var sut = new UserRepository(context);
            
            var user = await sut.GetAsync(_user.Id);
            
            Assert.NotNull(user);
            Assert.Equal(_user.Id, user.Id);
        }
    }
    
    [Fact]
    public async Task GetAsync_ById_UserDoesNotExist_ReturnsNull()
    {
        await using (var context = new AppDbContext(_options, _secretManager))
        {
            var sut = new UserRepository(context);
            
            var user = await sut.GetAsync(_user.Id);
            
            Assert.Null(user);
        }
    }
}