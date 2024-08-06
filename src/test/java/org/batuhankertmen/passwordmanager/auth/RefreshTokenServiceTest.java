package org.batuhankertmen.passwordmanager.auth;
import org.batuhankertmen.passwordmanager.common.exception.AuthTokenException;
import org.batuhankertmen.passwordmanager.user.Role;
import org.batuhankertmen.passwordmanager.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTokenForUser() {
        String token = "sampleToken";
        User user = User.builder()
                .username("testsaveuser")
                .password("password")
                .enabled(true)
                .firstName("Save")
                .lastName("User")
                .contact("savecontact")
                .role(Role.USER)
                .build();

        RefreshToken refreshToken = RefreshToken.builder().token(token).isValid(true).user(user).build();

        refreshTokenService.save(token, user);

        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void saveRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder().token("sampleToken").isValid(true).build();

        refreshTokenService.save(refreshToken);

        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void getRefreshTokenByToken_ValidToken() {
        String token = "validToken";
        RefreshToken refreshToken = RefreshToken.builder().token(token).isValid(true).build();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.getRefreshTokenByToken(token);

        assertEquals(refreshToken, result);
        verify(refreshTokenRepository).findByToken(token);
    }

    @Test
    void getRefreshTokenByToken_InvalidToken() {
        String token = "invalidToken";

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(AuthTokenException.class, () -> refreshTokenService.getRefreshTokenByToken(token));
        verify(refreshTokenRepository).findByToken(token);
    }

    @Test
    void invalidateRefreshTokensForUser() {
        int userId = 123;

        refreshTokenService.InvalidateRefreshTokensForUser(userId);

        verify(refreshTokenRepository).invalidateAllRefreshTokensByUser(userId);
    }
}
