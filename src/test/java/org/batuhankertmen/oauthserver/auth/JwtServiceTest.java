package org.batuhankertmen.oauthserver.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private PrivateKey privateKey;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(jwtService, "jwtIssuer", "test-issuer");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationDuration", 3600000L);

        privateKey = generateTestPrivateKey();
        Path tempPrivateKeyPath = Files.createTempFile("private_key", ".pem");
        Files.writeString(tempPrivateKeyPath, encodePrivateKey(privateKey));
        ReflectionTestUtils.setField(jwtService, "privateKeyPath", tempPrivateKeyPath.toString());
    }

    @Test
    void testCreateHexadecimalEncodedRandomBytes() {
        String randomBytes = JwtService.createHexadecimalEncodedRandomBytes(16);
        assertNotNull(randomBytes);
        assertEquals(32, randomBytes.length());
    }

    @Test
    void testGenerateAccessToken() throws Exception {
        when(userDetails.getUsername()).thenReturn("testUser");
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> headerClaims = new HashMap<>();
        String token = jwtService.generateAccessToken(claims, headerClaims, userDetails, "test-audience");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateIdToken() throws Exception {
        when(userDetails.getUsername()).thenReturn("testUser");
        HashMap<String, Object> claims = new HashMap<>();
        String token = jwtService.generateIdToken(claims, userDetails, "test-audience");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    private PrivateKey generateTestPrivateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair().getPrivate();
    }

    private String encodePrivateKey(PrivateKey privateKey) {
        return "-----BEGIN PRIVATE KEY-----\n" + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n-----END PRIVATE KEY-----";
    }
}
