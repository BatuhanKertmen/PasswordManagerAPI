using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using PasswordManager.Communications;
using PasswordManager.Database;
using PasswordManager.Facades;
using PasswordManager.Middlewares;
using PasswordManager.Models;
using PasswordManager.Repositories;
using PasswordManager.Services;

namespace PasswordManager;

public class BuilderServices
{
    public static void RegisterServices(WebApplicationBuilder builder)
    {
        builder.Services.AddControllers();
        builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());
        builder.Services.AddDbContext<AppDbContext>();
        builder.Services.AddTransient<UserActionsFacade>();
        builder.Services.AddTransient<LoginInformationFacade>();
        builder.Services.AddTransient<GlobalExceptionHandler>();
        builder.Services.AddTransient<IUserService, UserService>();
        builder.Services.AddTransient<IUserRepository, UserRepository>();
        builder.Services.AddTransient<IUserPasswordService, UserPasswordService>();
        builder.Services.AddTransient<IUserPasswordRepository, UserPasswordRepository>();
        builder.Services.AddTransient<IActivationCodeService, ActivationCodeService>();
        builder.Services.AddTransient<IActivationCodeRepository, ActivationCodeRepository>();
        builder.Services.AddTransient<ILoginInformationService, LoginInformationService>();
        builder.Services.AddTransient<ILoginInformationRepository, LoginInformationRepository>();
        builder.Services.AddTransient<ILoginInformationPasswordService, LoginInformationPasswordService>();
        builder.Services.AddTransient<ILoginInformationPasswordRepository, LoginInformationPasswordRepository>();
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
        builder.Services.AddSwaggerGen(options =>
        {
            options.SwaggerDoc("v1", new OpenApiInfo() { Title = "Password Manager API", Version = "v1"});
            options.AddSecurityDefinition(JwtBearerDefaults.AuthenticationScheme, new OpenApiSecurityScheme()
            {
                Name = "Authorization",
                In = ParameterLocation.Header,
                Type = SecuritySchemeType.ApiKey,
                Scheme = JwtBearerDefaults.AuthenticationScheme
            });
            options.AddSecurityRequirement(new OpenApiSecurityRequirement()
            {
                {
                    new OpenApiSecurityScheme()
                    {
                        Reference = new OpenApiReference()
                        {
                            Type = ReferenceType.SecurityScheme,
                            Id = JwtBearerDefaults.AuthenticationScheme
                        },
                        Scheme = "Oauth2",
                        Name = JwtBearerDefaults.AuthenticationScheme,
                        In = ParameterLocation.Header
                    },
                    new List<string>()
                }
            });
        });
    }
}