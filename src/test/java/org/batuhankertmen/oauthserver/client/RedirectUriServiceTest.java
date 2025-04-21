package org.batuhankertmen.oauthserver.client;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedirectUriServiceTest {

    @Mock
    private RedirectUriRepository redirectUriRepository;

    @InjectMocks
    private RedirectUriService redirectUriService;

    @Test
    void testValidate_ValidUriForServerApp() {
        assertTrue(redirectUriService.validate("https://valid.com", ClientType.SERVER_APP));
        assertTrue(redirectUriService.validate("http://localhost:8080", ClientType.SERVER_APP));
    }

    @Test
    void testValidate_InvalidUriWithFragment() {
        assertFalse(redirectUriService.validate("https://valid.com#fragment", ClientType.SERVER_APP));
    }

    @Test
    void testValidate_InvalidUriForServerApp() {
        assertFalse(redirectUriService.validate("ftp://invalid.com", ClientType.SERVER_APP));
        assertFalse(redirectUriService.validate("invalid.com", ClientType.SERVER_APP));
    }

    @Test
    void testFindByUri_UriExists() {
        RedirectUri redirectUri = new RedirectUri();
        redirectUri.setUri("https://valid.com");

        when(redirectUriRepository.findByUri("https://valid.com")).thenReturn(Optional.of(redirectUri));

        RedirectUri foundUri = redirectUriService.findByUri("https://valid.com");
        assertNotNull(foundUri);
        assertEquals("https://valid.com", foundUri.getUri());
    }

    @Test
    void testFindByUri_UriNotFound() {
        when(redirectUriRepository.findByUri("https://notfound.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> redirectUriService.findByUri("https://notfound.com"));
    }

    @Test
    void testIsRedirectUriRegisteredByClient_Exists() {
        when(redirectUriRepository.existsByUriAndClient_ClientId("https://valid.com", "client123"))
                .thenReturn(true);

        assertTrue(redirectUriService.isRedirectUriRegisteredByClient("https://valid.com", "client123"));
    }

    @Test
    void testIsRedirectUriRegisteredByClient_NotExists() {
        when(redirectUriRepository.existsByUriAndClient_ClientId("https://invalid.com", "client123"))
                .thenReturn(false);

        assertFalse(redirectUriService.isRedirectUriRegisteredByClient("https://invalid.com", "client123"));
    }
}