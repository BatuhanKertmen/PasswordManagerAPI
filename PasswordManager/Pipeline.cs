using PasswordManager.Middlewares;

namespace PasswordManager;

public class Pipeline
{
    public static void RegisterPipeline(WebApplication app)
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
}