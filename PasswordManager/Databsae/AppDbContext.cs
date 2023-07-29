using Microsoft.EntityFrameworkCore;
using PasswordManager.Models;

namespace PasswordManager.Database
{
    public class AppDbContext : DbContext
    {
        protected readonly IConfiguration configuration;

        public AppDbContext(IConfiguration configuration)
        {
            this.configuration = configuration;
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseNpgsql(configuration.GetConnectionString("PostgresConnectionString"));
        }

        public DbSet<User> Users { get; set; }
        public DbSet<ActivationCode> ActivationCodes { get; set; }
        public DbSet<LoginInformation> LoginInformations { get; set; }
    }
}
