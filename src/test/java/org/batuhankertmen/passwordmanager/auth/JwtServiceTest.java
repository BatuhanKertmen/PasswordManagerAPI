package org.batuhankertmen.passwordmanager.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private final String jwtSecretKey = "/RAw4D8q2PmcKKGxLb9ZdfQfldCNDfscyYg6ucc9sVo=";
    private final String jwtIssuer = "testIssuer";
    private final int accessTokenExpirationDuration = 900000;
    private final int refreshTokenExpirationDuration = 86400000;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", jwtSecretKey);
        ReflectionTestUtils.setField(jwtService, "jwtIssuer", jwtIssuer);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationDuration", accessTokenExpirationDuration);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpirationDuration", refreshTokenExpirationDuration);
    }

    @Test
    public void testExtractUsername() {
        String token = createToken("testuser");

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    public void testExtractClaim() {
        String token = createToken("testuser");

        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        assertThat(issuedAt).isNotNull();
    }

    @Test
    public void testGenerateJwtToken() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateAccessToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    public void testGenerateJwtTokenWithExtraClaims() {
        when(userDetails.getUsername()).thenReturn("testuser");

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "USER");

        String token = jwtService.generateAccessToken(extraClaims, userDetails);
        assertThat(token).isNotNull();

        Optional<String> role =  Optional.ofNullable(jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
        assertThat(role.isPresent()).isTrue();
        assertThat(role.get()).isEqualTo("USER");

        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    public void testIsJwtValid() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateAccessToken(userDetails);

        boolean isValid = jwtService.isJwtValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    public void testIsJwtInvalidWhenExpired() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = createExpiredToken("testuser");

        assertThatThrownBy(() -> jwtService.isJwtValid(token, userDetails));
    }

    @Test
    public void testGetJwtSigningKey() {
        Key key = jwtService.getJwtSigningKey();

        assertThat(key).isNotNull();
    }

    // Helper methods for creating tokens
    private String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationDuration))
                .setIssuer(jwtIssuer)
                .signWith(jwtService.getJwtSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createExpiredToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - accessTokenExpirationDuration * 2))
                .setExpiration(new Date(System.currentTimeMillis() - accessTokenExpirationDuration))
                .setIssuer(jwtIssuer)
                .signWith(jwtService.getJwtSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
