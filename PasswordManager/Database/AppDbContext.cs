using Microsoft.EntityFrameworkCore;
using PasswordManager.Exceptions;
using PasswordManager.Models;
using PasswordManager.Secrets;

namespace PasswordManager.Database
{
    public class AppDbContext : DbContext
    {
        private readonly ISecretManager _secretManager;
        
        public AppDbContext(DbContextOptions<AppDbContext> options, ISecretManager secretManager) : base(options)
        {
            _secretManager = secretManager;
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (optionsBuilder.IsConfigured == false)
            {
                var connectionString = _secretManager.GetPasswordManagerDatabaseConnectionString();
                if (connectionString == null)
                {
                    throw new SecretNotAvailableException();
                }
                
                optionsBuilder.UseNpgsql(connectionString);
            }
            base.OnConfiguring(optionsBuilder);
        }

        public DbSet<User> Users { get; set; }
        public DbSet<ActivationCode> ActivationCodes { get; set; }
        public DbSet<LoginInformation> LoginInformation { get; set; }
        public DbSet<UserPassword> UserPasswords { get; set; }
        public DbSet<LoginInformationPassword> LoginInformationPasswords { get; set; }
    }
}
