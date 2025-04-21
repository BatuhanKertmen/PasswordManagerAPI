package org.batuhankertmen.oauthserver.auth;

import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.common.exception.AuthException;
import org.batuhankertmen.oauthserver.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScopeServiceTest {

    @Mock
    private ScopeRepository scopeRepository;

    @InjectMocks
    private ScopeService scopeService;

    private User user;
    private Client client;

    @BeforeEach
    void setUp() {
        user = new User();
        client = new Client();
    }

    @Test
    void testSaveAll_SavesNewScopes() {
        Scope scope1 = Scope.builder().scope("openid").build();
        Scope scope2 = Scope.builder().scope("profile").build();
        Set<Scope> scopes = new HashSet<>(List.of(scope1, scope2));

        when(scopeRepository.findAllByUserAndClient(user, client)).thenReturn(new HashSet<>());

        scopeService.saveAll(scopes, user, client);

        verify(scopeRepository).saveAll(scopes);
    }

    @Test
    void testGetAllScopesByUserAndClient() {
        HashSet<Scope> expectedScopes = new HashSet<>(List.of(Scope.builder().scope("openid").build()));
        when(scopeRepository.findAllByUserAndClient(user, client)).thenReturn(expectedScopes);

        Set<Scope> actualScopes = scopeService.getAllScopesByUserAndClient(user, client);
        assertEquals(expectedScopes, actualScopes);
    }

    @Test
    void testGetAllScopesByUserAndClientId() {
        HashSet<Scope> expectedScopes = new HashSet<>(List.of(Scope.builder().scope("email").build()));
        when(scopeRepository.findAllByUserAndClient_ClientId(user, "client123")).thenReturn(expectedScopes);

        Set<Scope> actualScopes = scopeService.getAllScopesByUserAndClientId(user, "client123");
        assertEquals(expectedScopes, actualScopes);
    }

    @Test
    void testGenerateScope_ValidScope() {
        Scope scope = scopeService.generateScope("openid");
        assertNotNull(scope);
        assertEquals("openid", scope.getScope());
    }

    @Test
    void testGenerateScope_InvalidScopeThrowsException() {
        assertThrows(AuthException.class, () -> scopeService.generateScope("invalid_scope"));
    }

    @Test
    void testIsScopeValid() {
        assertTrue(scopeService.isScopeValid("openid"));
        assertFalse(scopeService.isScopeValid("invalid_scope"));
    }

    @Test
    void testDeleteAllScopesByUserAndClient() {
        HashSet<Scope> scopes = new HashSet<>(List.of(Scope.builder().scope("openid").build()));
        when(scopeRepository.findAllByUserAndClient(user, client)).thenReturn(scopes);

        scopeService.deleteAllScopesByUserAndClient(user, client);

        verify(scopeRepository).findAllByUserAndClient(user, client);
    }
}
