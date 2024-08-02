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

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails);

    boolean isJwtValid(String jwt, UserDetails userDetails);
}
