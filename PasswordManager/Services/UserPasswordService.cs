using PasswordManager.Utilities;
using PasswordManager.Models;
using PasswordManager.Repositories;
using System.Security.Cryptography;
using PasswordManager.Exceptions;
using PasswordManager.Secrets;

namespace PasswordManager.Services
{
    public class UserPasswordService : IUserPasswordService
    {
        private readonly ISecretManager _secretManager;
        private readonly IUserPasswordRepository _userPasswordRepository;

        public UserPasswordService(IUserPasswordRepository userPasswordRepository, ISecretManager secretManager)
        {
            _secretManager = secretManager;
            _userPasswordRepository = userPasswordRepository;
        }

        public async Task<UserPassword> SaveAsync(string passwordHexString, UserPassword userPassword, User passwordOwner)
        {
            var nonce = UtilityFunctions.GenerateRandomBytes(12);
            userPassword.Nonce = nonce;

            var encryptPasswordOutput = EncryptPassword(
                passwordHexString,
                nonce
            );
            userPassword.Password = encryptPasswordOutput.EncryptedPassword;
            userPassword.Tag = encryptPasswordOutput.Tag;

            userPassword.UserId = passwordOwner.Id;
            userPassword.User = passwordOwner;
            
            userPassword = await _userPasswordRepository.SaveAsync(userPassword);
            return userPassword;
        }

        public async Task<bool> CheckPasswordAsync(Guid userId, string password)
        {
            var userPassword = await _userPasswordRepository.GetAsync(userId);
            if (userPassword == null)
            {
                throw new UserPasswordNotFoundException();
            }
            
            var encryptPasswordOutput = EncryptPassword(password, userPassword);

            if (encryptPasswordOutput.Tag.SequenceEqual(userPassword.Tag) == false)
            {
                return false;
            }

            if (encryptPasswordOutput.EncryptedPassword.SequenceEqual(userPassword.Password) == false) 
            {
                return false;
            }

            return true;
        }

        private EncryptPasswordOutputWrapper EncryptPassword(string passwordHash, UserPassword userPassword)
        {
            return EncryptPassword(
                passwordHash,
                userPassword.Nonce
            );
        }

        private EncryptPasswordOutputWrapper EncryptPassword(
            string passwordHexString,
            byte[] nonce
        )
        {
            byte[] passwordHashBytes = Convert.FromHexString(passwordHexString);
            var encryptedHashedPassword = new byte[passwordHashBytes.Length];
            var tag = new byte[16];

            var privateKey = _secretManager.GetPepperSymmetricKey();
            if (privateKey == null)
            {
                throw new SecretNotAvailableException();
            }
            using var aes = new AesGcm(privateKey);
            {
                aes.Encrypt(nonce, passwordHashBytes, encryptedHashedPassword, tag);
            }

            return new EncryptPasswordOutputWrapper
            {
                Tag = tag,
                EncryptedPassword = encryptedHashedPassword
            };
        }
    }

    public class EncryptPasswordOutputWrapper
    {
        public byte[] EncryptedPassword { get; init; }
        public byte[] Tag { get; init; }
    }
}