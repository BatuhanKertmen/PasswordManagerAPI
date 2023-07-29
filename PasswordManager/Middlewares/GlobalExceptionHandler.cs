using Microsoft.AspNetCore.Mvc;
using PasswordManager.Exceptions;
using System.Net;
using System.Net.Mail;
using System.Text.Json;

namespace PasswordManager.Middlewares
{
    
    public class GlobalExceptionHandler : IMiddleware
    {
        // TODO: DI Logger

        readonly static string ServerError = "Server Error";
        readonly static string UserError = "User Error";

        public async Task InvokeAsync(HttpContext context, RequestDelegate next)
        {
            try
            {
                await next(context);
            }
            catch (EmailAlreadyExistsException e)
            {
           

                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.Conflict,
                    UserError,
                    "Email already exists",
                    "The provided email address is already registered in our system."
                );
            }
            catch (Exception e)
            {
               

                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.InternalServerError,
                    ServerError,
                    "Server Error",
                    "An internal server error has occured!"
                );    
            }
        }

        private async void UpdateHttpContext(HttpContext context, int statusCode, string type, string title, string detail)
        {
            context.Response.StatusCode = statusCode;
            context.Response.ContentType = "application/json";

            ProblemDetails problem = new ProblemDetails()
            {
                Status = statusCode,
                Type = type,
                Title = title,
                Detail = detail
            };
            string problemJson = JsonSerializer.Serialize(problem);

            await context.Response.WriteAsync(problemJson);
        }
    }

    
}
