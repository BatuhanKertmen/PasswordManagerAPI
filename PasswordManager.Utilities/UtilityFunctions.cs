using System.Security.Cryptography;

namespace PasswordManager.Utilities;

public static class UtilityFunctions
{
    public static byte[] GenerateRandomBytes(int length)
    {
       return RandomNumberGenerator.GetBytes(length);
    }
}