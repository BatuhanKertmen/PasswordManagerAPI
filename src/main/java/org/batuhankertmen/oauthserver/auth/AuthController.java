package org.batuhankertmen.oauthserver.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.user.UserDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthenticationService authenticationService;
    private final IAuthorizationService authorizationService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegistryRequestDto registryRequestDto,
                                            HttpServletRequest httpRequest,
                                            HttpServletResponse httpResponse
    ) throws URISyntaxException {
        return ResponseEntity
                .ok()
                .body(authenticationService.register(registryRequestDto, httpRequest, httpResponse));
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> token(@RequestBody TokenRequestDto tokenRequestDto)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(authorizationService.generateAccessToken(tokenRequestDto));
    }

}
