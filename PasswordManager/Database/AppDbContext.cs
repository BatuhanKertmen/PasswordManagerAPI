using Microsoft.EntityFrameworkCore;
using PasswordManager.Models;

namespace PasswordManager.Database
{
    public class AppDbContext : DbContext
    {
        private readonly IConfiguration _configuration;
        public AppDbContext(DbContextOptions<AppDbContext> options, IConfiguration configuration) : base(options)
        {
            _configuration = configuration;
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (optionsBuilder.IsConfigured == false)
            {
                optionsBuilder.UseNpgsql(_configuration.GetConnectionString("PostgresConnectionString"));
                base.OnConfiguring(optionsBuilder);
            }
        }

        public DbSet<User> Users { get; set; }
        public DbSet<ActivationCode> ActivationCodes { get; set; }
        public DbSet<LoginInformation> LoginInformation { get; set; }
        public DbSet<UserPassword> UserPasswords { get; set; }
        public DbSet<LoginInformationPassword> LoginInformationPasswords { get; set; }
    }
}
