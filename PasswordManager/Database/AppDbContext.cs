using Microsoft.EntityFrameworkCore;
using PasswordManager.Models;

namespace PasswordManager.Database
{
    public class AppDbContext : DbContext
    {
        private readonly IConfiguration _configuration;

        public AppDbContext(IConfiguration configuration)
        {
            _configuration = configuration;
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseNpgsql(_configuration.GetConnectionString("PostgresConnectionString"));
        }

        public DbSet<User> Users { get; set; }
        public DbSet<ActivationCode> ActivationCodes { get; set; }
        public DbSet<LoginInformation> LoginInformation { get; set; }
    }
}
