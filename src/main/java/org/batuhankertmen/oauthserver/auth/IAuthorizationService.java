package org.batuhankertmen.oauthserver.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface IAuthorizationService {

    String processAuthorization(HttpSession session);

    TokenResponseDto generateAccessToken(TokenRequestDto tokenRequestDto)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException;
}
