using Konscious.Security.Cryptography;
using PasswordManager.Utilities;
using PasswordManager.Models;
using PasswordManager.Repositories;
using System.Security.Cryptography;
using System.Text;
using PasswordManager.Exceptions;

namespace PasswordManager.Services
{
    public class UserPasswordService : IUserPasswordService
    {
        private readonly IConfiguration _configuration;
        private readonly IUserPasswordRepository _userPasswordRepository;

        public UserPasswordService(IConfiguration configuration, IUserPasswordRepository userPasswordRepository)
        {
            _configuration = configuration;
            _userPasswordRepository = userPasswordRepository;
        }

        public async Task<UserPassword> SaveAsync(string password, User passwordOwner)
        {
            var salt = UtilityFunctions.GenerateRandomBytes(32);
            var nonce = UtilityFunctions.GenerateRandomBytes(12);
            var tag = new byte[16];

            var encryptedHashedPassword = SaltAndEncryptPassword(
                password,
                salt,
                (int)Argon2idStandards.MemorySize,
                (int)Argon2idStandards.DegreeOfParallelism,
                (int)Argon2idStandards.Iterations,
                nonce,
                out tag
            );
            
            var userPassword = new UserPassword()
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

            userPassword = await _userPasswordRepository.SaveAsync(userPassword);
            return userPassword;
        }

        private byte[] SaltAndEncryptPassword(string password, UserPassword userPassword, out byte[] tag)
        {
            return SaltAndEncryptPassword(
                password,
                userPassword.Salt,
                userPassword.MemorySize,
                userPassword.DegreeOfParallism,
                userPassword.Iterations,
                userPassword.Nonce,
                out tag
            );
        }

        private byte[] SaltAndEncryptPassword(
            string password,
            byte[] salt,
            int memorySize,
            int degreeOfParallelism,
            int iterations,
            byte[] nonce,
            out byte[] tag
        )
        {
            using var argon2id = new Argon2id(Encoding.UTF8.GetBytes(password));
            {
                argon2id.Salt = salt;
                argon2id.MemorySize = memorySize;
                argon2id.DegreeOfParallelism = degreeOfParallelism;
                argon2id.Iterations = iterations;
            }

            var hashedPassword = argon2id.GetBytes(128);
            
            var encryptedHashedPassword = new byte[hashedPassword.Length];
            tag = new byte[16];

            using var aes = new AesGcm(Convert.FromHexString(_configuration.GetValue<string>("pepper:key")));
            {
                aes.Encrypt(nonce, hashedPassword, encryptedHashedPassword, tag);
            }

            return encryptedHashedPassword;
        }
    }
}