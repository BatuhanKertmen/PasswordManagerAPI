package org.batuhankertmen.passwordmanager.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService implements IJwtService{

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    @Value("${security.jwt.expiration}")
    private long accessTokenExpirationDuration;

    @Value("${security.jwt.refresh-token.expiration}")
    private long refreshTokenExpirationDuration;

    @Override
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    @Override
    public Key getJwtSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public <T> T extractClaim(String jwt, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimResolver.apply(claims);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put("token_type", "accessToken");
        return buildToken(extraClaims, userDetails, accessTokenExpirationDuration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .claim("nonce", UUID.randomUUID().toString())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setIssuer(jwtIssuer)
                .signWith(getJwtSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("token_type", "refreshToken");
        return buildToken(extraClaims, userDetails, refreshTokenExpirationDuration);
    }

    @Override
    public boolean isJwtValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String jwt) {
        Date expiryDate = extractClaim(jwt, Claims::getExpiration);
        return expiryDate.before(new Date());
    }


    private Claims extractAllClaims(String jwt) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getJwtSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
