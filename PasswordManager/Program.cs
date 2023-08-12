namespace PasswordManager
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);
            BuilderServices.RegisterServices(builder);

            var app = builder.Build();
            Pipeline.RegisterPipeline(app);
            
            app.Run();
        }
    }
}