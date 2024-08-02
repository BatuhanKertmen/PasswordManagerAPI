package org.batuhankertmen.passwordmanager.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthenticationService {

    AuthenticationResponseDto register(RegistryRequestDto request);

    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);

    AuthenticationResponseDto refresh(HttpServletRequest request, HttpServletResponse response);
}
