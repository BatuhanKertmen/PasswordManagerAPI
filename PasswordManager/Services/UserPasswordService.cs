using Konscious.Security.Cryptography;
using Microsoft.Extensions.Configuration;
using PasswordManager.Database;
using PasswordManager.Models;
using PasswordManager.Repositories;
using System.Security.Cryptography;
using System.Text;

namespace PasswordManager.Services
{
    public enum Argon2idStandards
    {
        MemorySize = 1024 * 19, // 19MB
        DegreeOfParallelism = 1,
        Iterations = 2
    }
    public class UserPasswordService : IUserPasswordService
    {
        private readonly IConfiguration _configuration;
        private readonly IUserPasswordRepository _userPasswordRepository;

        public UserPasswordService(IConfiguration configuration, IUserPasswordRepository userPasswordRepository)
        {
            _configuration = configuration;
            _userPasswordRepository = userPasswordRepository;
        }

        public UserPassword Save(string password, User passwordOwner)
        {
            byte[] salt = GenerateRandomBytes(32);

            using Argon2id argon2id = new Argon2id(Encoding.UTF8.GetBytes(password));
            {
                argon2id.Salt = salt;
                argon2id.MemorySize = (int) Argon2idStandards.MemorySize;
                argon2id.DegreeOfParallelism = (int) Argon2idStandards.DegreeOfParallelism;
                argon2id.Iterations = (int) Argon2idStandards.Iterations;
            }

            byte[] hashedPassword = argon2id.GetBytes(128);

            byte[] encryptedHashedPassword;
            byte[] tag;

            byte[] nonce = GenerateRandomBytes(12);
            encryptedHashedPassword = new byte[hashedPassword.Length];
            tag = new byte[16];

            using AesGcm aes = new AesGcm(Convert.FromHexString(_configuration.GetValue<string>("pepper:key")));
            {
                aes.Encrypt(nonce, hashedPassword, encryptedHashedPassword, tag);
            }

            UserPassword userPassword = new UserPassword()
            {
                Password = encryptedHashedPassword,
                Salt = salt,
                MemorySize = (int)Argon2idStandards.MemorySize,
                DegreeOfParallism = (int)Argon2idStandards.DegreeOfParallelism,
                Iterations = (int)Argon2idStandards.Iterations,
                Nonce = nonce,
                Tag = tag,
                
                UserId = passwordOwner.Id,
                User = passwordOwner
            };

            userPassword = _userPasswordRepository.Save(userPassword);
            return userPassword;
        }


        private byte[] GenerateRandomBytes(int byteLength)
        {
            byte[] saltBuffer = RandomNumberGenerator.GetBytes(byteLength);
            return saltBuffer;
        }
    }
}
