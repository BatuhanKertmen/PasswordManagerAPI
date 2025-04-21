package org.batuhankertmen.oauthserver.user;

import org.batuhankertmen.oauthserver.auth.IScopeService;
import org.batuhankertmen.oauthserver.auth.Scope;
import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.client.ClientRepository;
import org.batuhankertmen.oauthserver.common.exception.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private IScopeService scopeService;

    @InjectMocks
    private UserService userService;

    private User user;
    private Client client;
    private Scope scope;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setEnabled(true);

        client = new Client();
        client.setClientId("client123");

        scope = new Scope();
        scope.setClient(client);
    }

    @Test
    void testGetAllowedScopes_Success() {
        when(userRepository.findByUsernameAndEnabledTrue("testUser")).thenReturn(Optional.of(user));
        when(scopeService.getAllScopesByUserAndClientId(user, "client123")).thenReturn(new HashSet<>(Collections.singleton(scope)));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn("testUser");

        SecurityContextHolder.setContext(securityContext);

        Set<Scope> scopes = userService.getAllowedScopes("client123");

        assertEquals(1, scopes.size());
        verify(scopeService).getAllScopesByUserAndClientId(user, "client123");
    }

    @Test
    void testGetAllowedScopesPerClient_Success() {
        user.setScopes(Collections.singleton(scope));
        when(userRepository.findByUsernameAndEnabledTrue("testUser")).thenReturn(Optional.of(user));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        Map<Client, List<Scope>> result = userService.getAllowedScopesPerClient();
        assertEquals(1, result.size());
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        when(userRepository.findByUsernameAndEnabledTrue("testUser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testUser");
        assertNotNull(userDetails);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsernameAndEnabledTrue("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknownUser"));
    }

    @Test
    void testRevokeClientPermissions_Success() {
        when(userRepository.findByUsernameAndEnabledTrue("testUser")).thenReturn(Optional.of(user));
        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        userService.revokeClientPermissions("client123");

        verify(scopeService).deleteAllScopesByUserAndClient(user, client);
    }

    @Test
    void testRevokeClientPermissions_ClientNotFound() {
        when(userRepository.findByUsernameAndEnabledTrue("testUser")).thenReturn(Optional.of(user));
        when(clientRepository.findById("client123")).thenReturn(Optional.empty());

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        assertThrows(ClientException.class, () -> userService.revokeClientPermissions("client123"));
    }
}

