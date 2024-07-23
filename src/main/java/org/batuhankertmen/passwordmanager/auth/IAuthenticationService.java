package org.batuhankertmen.passwordmanager.auth;

public interface IAuthenticationService {

    AuthenticationResponseDto register(RegistryRequestDto request);

    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);
}
