package org.batuhankertmen.oauthserver.auth;

import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.common.exception.AuthException;
import org.batuhankertmen.oauthserver.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private RefreshToken refreshToken;
    private User user;
    private Client client;

    @BeforeEach
    void setUp() {
        user = new User();
        client = new Client();
        refreshToken = RefreshToken.builder()
                .active(true)
                .code("test-refresh-token")
                .user(user)
                .client(client)
                .build();
    }

    @Test
    void testFindByCode_TokenExistsAndActive() {
        when(refreshTokenRepository.findByCode("test-refresh-token")).thenReturn(Optional.of(refreshToken));
        RefreshToken foundToken = refreshTokenService.findByCode("test-refresh-token");
        assertNotNull(foundToken);
        assertEquals("test-refresh-token", foundToken.getCode());
    }

    @Test
    void testFindByCode_TokenNotFound() {
        when(refreshTokenRepository.findByCode("invalid-token")).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> refreshTokenService.findByCode("invalid-token"));
    }

    @Test
    void testFindByCode_TokenInactive() {
        refreshToken.setActive(false);
        when(refreshTokenRepository.findByCode("test-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.findByUserAndClientAndActiveTrue(user, client)).thenReturn(List.of());

        assertThrows(AuthException.class, () -> refreshTokenService.findByCode("test-refresh-token"));
    }

    @Test
    void testCreateRefreshToken() {
        RefreshToken newToken = refreshTokenService.createRefreshToken();
        assertNotNull(newToken);
        assertTrue(newToken.isActive());
        assertNotNull(newToken.getCode());
    }

    @Test
    void testSaveRefreshToken() {
        refreshTokenService.save(refreshToken);
        verify(refreshTokenRepository, times(1)).save(refreshToken);
    }

    @Test
    void testDisableRefreshToken() {
        refreshTokenService.disable(refreshToken);
        assertFalse(refreshToken.isActive());
        verify(refreshTokenRepository, times(1)).save(refreshToken);
    }
}