package org.batuhankertmen.passwordmanager.auth;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Map;
import java.util.function.Function;

public interface IJwtService {
    String extractUsername(String jwt);

    Key getJwtSigningKey();

    <T> T  extractClaim(String token, Function<Claims, T> claimResolver);

    String generateJwtToken(UserDetails userDetails);

    String generateJwtToken(Map<String, Object> extraClaims, UserDetails userDetails);

    boolean isJwtValid(String jwt, UserDetails userDetails);
}
