﻿using PasswordManager.Models;

namespace PasswordManager.Services;

public interface ILoginInformationService
{
    Task<LoginInformation> SaveAsync(LoginInformation loginInformation);
    Task<List<LoginInformation>> GelAllByDomainAsync(string domain);
}