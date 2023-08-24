using PasswordManager.Utilities;
using PasswordManager.Models;
using PasswordManager.Repositories;
using System.Security.Cryptography;
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

        public async Task<UserPassword> SaveAsync(string passwordHexString, UserPassword userPassword, User passwordOwner)
        {
            var nonce = UtilityFunctions.GenerateRandomBytes(12);
            var tag = new byte[16];
            userPassword.Nonce = nonce;
            userPassword.Tag = tag;
            
            var encryptedHashedPassword = EncryptPassword(
                passwordHexString,
                nonce,
                out tag
            );
            userPassword.Password = encryptedHashedPassword;

            userPassword.UserId = passwordOwner.Id;
            userPassword.User = passwordOwner;
            
            userPassword = await _userPasswordRepository.SaveAsync(userPassword);
            return userPassword;
        }

        public async Task<bool> CheckPassword(Guid userId, string password)
        {
            var userPassword = await _userPasswordRepository.GetAsync(userId);
            if (userPassword == null)
            {
                throw new UserPasswordNotFoundException();
            }

            var tag = new byte[16];
            var encryptedHashedPassword = EncryptPassword(password, userPassword, out tag);

            if (tag.SequenceEqual(userPassword.Tag) == false)
            {
                return false;
            }

            if (encryptedHashedPassword.SequenceEqual(userPassword.Password) == false) 
            {
                return false;
            }

            return true;
        }

        private byte[] EncryptPassword(string passwordHash, UserPassword userPassword, out byte[] tag)
        {
            return EncryptPassword(
                passwordHash,
                userPassword.Nonce,
                out tag
            );
        }

        private byte[] EncryptPassword(
            string passwordHexString,
            byte[] nonce,
            out byte[] tag
        )
        {
            byte[] passwordHashBytes = Convert.FromHexString(passwordHexString);
            var encryptedHashedPassword = new byte[passwordHashBytes.Length];
            tag = new byte[16];

            using var aes = new AesGcm(Convert.FromHexString(_configuration.GetValue<string>("pepper:key")));
            {
                aes.Encrypt(nonce, passwordHashBytes, encryptedHashedPassword, tag);
            }

            return encryptedHashedPassword;
        }
    }
}