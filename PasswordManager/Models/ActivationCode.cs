﻿using System.ComponentModel.DataAnnotations;

namespace PasswordManager.Models
{
    public class ActivationCode
    {
        [Key]
        public Guid Id { get; set; }
        public string Code { get; set; }
        public DateTime ExpiryDate { get; set; }
        public DateTime Created { get; set; }

        public Guid UserId { get; set; }
        public User User { get; set; }
    }
}
