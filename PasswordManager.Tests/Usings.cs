global using System.IdentityModel.Tokens.Jwt;
global using System.Security.Claims;
global using System.Text;

global using Microsoft.AspNetCore.Http;
global using Microsoft.AspNetCore.Routing;
global using Microsoft.EntityFrameworkCore;

global using AutoMapper;

global using Moq;
global using Xunit;

global using PasswordManager.Communications;
global using PasswordManager.Database;
global using PasswordManager.DTO;
global using PasswordManager.Exceptions;
global using PasswordManager.Facades;
global using PasswordManager.Repositories;
global using PasswordManager.Models;
global using PasswordManager.Secrets;
global using PasswordManager.Secrets.FileBased;
global using PasswordManager.Services;
