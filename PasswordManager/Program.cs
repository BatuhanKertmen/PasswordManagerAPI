using PasswordManager.Database;
using PasswordManager.Facades;
using PasswordManager.Middlewares;
using PasswordManager.Repositories;
using PasswordManager.Services;

namespace PasswordManager
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);
            { 
                builder.Services.AddControllers();
                builder.Services.AddDbContext<AppDbContext>();
                builder.Services.AddTransient<RegisterFacade>();
                builder.Services.AddTransient<GlobalExceptionHandler>();
                builder.Services.AddTransient<IUserService, UserService>();
                builder.Services.AddTransient<IUserRepository, UserRepository>();
                builder.Services.AddTransient<IUserPasswordService, UserPasswordService>();
                builder.Services.AddTransient<IUserPasswordRepository, UserPasswordRepository>();
                builder.Services.AddSwaggerGen();
            }

            var app = builder.Build();
            {
                if (app.Environment.IsDevelopment())
                {
                    app.UseSwagger();
                    app.UseSwaggerUI();
                }

                app.UseHttpsRedirection();
                app.UseMiddleware<GlobalExceptionHandler>();
                app.MapControllers();
            }

            app.Run();
        }
    }
}