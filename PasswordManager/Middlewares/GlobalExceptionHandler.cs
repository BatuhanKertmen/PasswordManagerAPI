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
            catch (AccountNotActivatedException e)
            {
                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.Forbidden,
                    UserError,
                    "Account not Activated",
                    e.StackTrace
                );
            }
            catch (EmailOrPasswordIsIncorrectException e)
            {
                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.BadRequest,
                    UserError,
                    "Email or Password is incorrect",
                    e.StackTrace
                );
            }

            catch (EmailAlreadyExistsException e)
            {
                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.Conflict,
                    UserError,
                    "Email already exists",
                    e.StackTrace
                );
            }
            catch (UserPasswordNotFoundException e)
            {
                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.NotFound,
                    ServerError,
                    "User Password Not Found",
                    e.StackTrace
                );
            }
            catch (SecretNotAvailableException e)
            {
                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.ServiceUnavailable,
                    ServerError,
                    "Server Can Not Reach Secrets",
                    e.StackTrace
                );
            }
            catch (UserNotFoundException e)
            {
                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.InternalServerError,
                    ServerError,
                    "User Not Found",
                    e.StackTrace
                );
            }
            /*
            catch (Exception e)
            {
                UpdateHttpContext(
                    context,
                    (int)HttpStatusCode.InternalServerError,
                    ServerError,
                    "Server Error",
                    e.StackTrace
                );    
            }*/
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
