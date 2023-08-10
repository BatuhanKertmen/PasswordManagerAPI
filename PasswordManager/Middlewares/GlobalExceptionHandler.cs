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
        

        private const string ServerError = "Server Error";
        private const string UserError = "User Error";

        public async Task InvokeAsync(HttpContext context, RequestDelegate next)
        {
            try
            {
                await next(context);
            }
            catch (EmailOrPasswordIsIncorrectException)
            {
                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.BadRequest,
                    UserError,
                    "Email or Password is incorrect",
                    "The provided email or password does not match."
                );
            }
            
            catch (EmailAlreadyExistsException)
            {
           

                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.Conflict,
                    UserError,
                    "Email already exists",
                    "The provided email address is already registered in our system."
                );
            }
            catch (Exception )
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
