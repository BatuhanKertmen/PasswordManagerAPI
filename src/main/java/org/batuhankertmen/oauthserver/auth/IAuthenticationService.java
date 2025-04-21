package org.batuhankertmen.oauthserver.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.batuhankertmen.oauthserver.user.UserDto;

public interface IAuthenticationService {

    UserDto register(RegistryRequestDto registryRequestDto, HttpServletRequest request
            , HttpServletResponse response);
}
