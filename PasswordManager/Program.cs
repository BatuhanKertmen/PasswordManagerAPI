using System.Text;
using AutoMapper;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using PasswordManager.Communications;
using PasswordManager.Database;
using PasswordManager.DTO;
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
                builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());
                builder.Services.AddDbContext<AppDbContext>();
                builder.Services.AddTransient<UserActionsFacade>();
                builder.Services.AddTransient<GlobalExceptionHandler>();
                builder.Services.AddTransient<IUserService, UserService>();
                builder.Services.AddTransient<IUserRepository, UserRepository>();
                builder.Services.AddTransient<IUserPasswordService, UserPasswordService>();
                builder.Services.AddTransient<IUserPasswordRepository, UserPasswordRepository>();
                builder.Services.AddTransient<IActivationCodeService, ActivationCodeService>();
                builder.Services.AddTransient<IActivationCodeRepository, ActivationCodeRepository>();
                builder.Services.AddTransient<ICommunicationChannel, Mail>();
                builder.Services.AddTransient<IJwtService, JwtService>();
                builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                    .AddJwtBearer(options =>
                        options.TokenValidationParameters = new TokenValidationParameters
                        {
                            ValidateIssuer = true,
                            ValidateAudience = true,
                            ValidateLifetime = true,
                            ValidateIssuerSigningKey = true,
                            ValidIssuer = builder.Configuration["JWT:issuer"],
                            ValidAudience = builder.Configuration["JWT:audience"],
                            IssuerSigningKey =
                                new SymmetricSecurityKey(Encoding.UTF8.GetBytes(builder.Configuration["JWT:key"]))
                        });
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

                app.UseAuthentication();
                app.UseAuthorization();
                
                app.UseMiddleware<GlobalExceptionHandler>();
                app.MapControllers();
            }

            app.Run();
        }
    }
}