package org.batuhankertmen.oauthserver.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

// TODO: is not testable

@Service
@RequiredArgsConstructor
public class JwtService implements IJwtService{

    private static final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    @Value("${security.jwt.expiration}")
    private long accessTokenExpirationDuration;

    public static String createHexadecimalEncodedRandomBytes(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }

    private Key getJwtSigningKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String key = Files.readString(Paths.get(privateKeyPath));

        // Clean up the key string and decode
        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);

        // Convert to PrivateKey object
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    @Override
    public String generateAccessToken(Map<String, Object> extraClaims, Map<String, Object> extraHeaderClaims, UserDetails userDetails,
                                      String audience) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        extraClaims.put("type", "access_token");
        return buildToken(extraClaims, extraHeaderClaims, userDetails, accessTokenExpirationDuration, audience);
    }

    @Override
    public String generateIdToken(HashMap<String, Object> claims, UserDetails userDetails, String audience)throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        claims.put("type", "id_token");

        return buildToken(
                claims,
                new HashMap<>(),
                userDetails,
                accessTokenExpirationDuration,
                audience
        );
    }

    @Override
    public String generateIdToken(HashMap<String, Object> claims, UserDetails userDetails,String audience, String nonce) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        claims.put("type", "id_token");
        claims.put("nonce", nonce);

        return buildToken(
                claims,
                new HashMap<>(),
                userDetails,
                accessTokenExpirationDuration,
                audience
        );
    }

    private String buildToken(Map<String, Object> extraClaims, Map<String, Object> extraHeaderClaims, UserDetails userDetails, long expiration, String audience) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setHeader(extraHeaderClaims)
                .setAudience(audience)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setIssuer(jwtIssuer)
                .signWith(getJwtSigningKey(), SignatureAlgorithm.RS512)
                .compact();
    }

    @Override
    public String getExpirationDate() {
        return String.valueOf(accessTokenExpirationDuration);
    }
}
