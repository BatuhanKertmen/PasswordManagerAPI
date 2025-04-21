package org.batuhankertmen.oauthserver.client;

import jakarta.servlet.http.HttpSession;
import org.batuhankertmen.oauthserver.auth.IScopeService;
import org.batuhankertmen.oauthserver.auth.Scope;
import org.batuhankertmen.oauthserver.common.exception.AuthException;
import org.batuhankertmen.oauthserver.common.exception.ClientException;
import org.batuhankertmen.oauthserver.common.exception.RestException;
import org.batuhankertmen.oauthserver.common.exception.UserException;
import org.batuhankertmen.oauthserver.company.Company;
import org.batuhankertmen.oauthserver.company.CompanyRepository;
import org.batuhankertmen.oauthserver.user.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private IRedirectUriService redirectUriService;

    @Mock
    private IUserService userService;

    @Mock
    private IScopeService scopeService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setClientId("client123");
        client.setClientSecret("encodedSecret");
    }

    @Test
    void testFindClientById_ClientExists() {
        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));

        Client foundClient = clientService.findClientById("client123");

        assertNotNull(foundClient);
        assertEquals("client123", foundClient.getClientId());
    }

    @Test
    void testFindClientById_ClientNotFound() {
        when(clientRepository.findById("unknownClient")).thenReturn(Optional.empty());

        assertThrows(ClientException.class, () -> clientService.findClientById("unknownClient"));
    }

    @Test
    void testCheckClientSecret_ValidSecret() {
        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("rawSecret", "encodedSecret")).thenReturn(true);

        boolean result = clientService.checkClientSecret("client123", "rawSecret");
        assertTrue(result);
    }

    @Test
    void testCheckClientSecret_InvalidSecret() {
        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrongSecret", "encodedSecret")).thenReturn(false);

        boolean result = clientService.checkClientSecret("client123", "wrongSecret");
        assertFalse(result);
    }

    @Test
    void testCheckClientSecret_ClientNotFound() {
        when(clientRepository.findById("client123")).thenReturn(Optional.empty());

        assertThrows(ClientException.class, () -> clientService.checkClientSecret("client123", "secret"));
    }

    @Test
    void testAuthorizeClient_InvalidRedirectUri() {
        when(redirectUriService.isRedirectUriRegisteredByClient("https://invalid.com", "client123"))
                .thenReturn(false);

        assertThrows(ClientException.class, () ->
                clientService.authorizeClient(session, "client123", "https://invalid.com",
                        "code", "openid", "state", "codeChallenge", "S256", "nonce"));
    }

    @Test
    void testAuthorizeClient_InvalidResponseType() {
        when(redirectUriService.isRedirectUriRegisteredByClient("https://valid.com", "client123"))
                .thenReturn(true);

        RedirectView redirectView = clientService.authorizeClient(session, "client123", "https://valid.com",
                "invalid_type", "openid", "state", "codeChallenge", "S256", "nonce");

        assertTrue(redirectView.getUrl().contains("error=invalid_request"));
    }

    @Test
    void testAuthorizeClient_OpenIdScopeButNoNonce_BadRequest() {
        when(redirectUriService.isRedirectUriRegisteredByClient("https://invalid.com", "client123"))
                .thenReturn(true);


        assertThrows(RestException.class, () ->
                clientService.authorizeClient(session, "client123",
                        "https://invalid.com",
                        "code",
                        "openid",
                        "state",
                        "codeChallenge",
                        "S256",
                        null));
    }


    @Test
    void testAuthorizeClient_InvalidScope() {
        when(redirectUriService.isRedirectUriRegisteredByClient("https://invalid.com", "client123"))
                .thenReturn(true);

        assertThrows(AuthException.class, () ->
                clientService.authorizeClient(session, "client123",
                        "https://invalid.com",
                        "code",
                        "invalidScope",
                        "state",
                        "codeChallenge",
                        "S256",
                        null));
    }

    @Test
    void testAuthorizeClient_StoresSessionAttributes() {
        when(redirectUriService.isRedirectUriRegisteredByClient("https://valid.com", "client123"))
                .thenReturn(true);
        when(scopeService.isScopeValid("openid")).thenReturn(true);
        when(userService.getAllowedScopes("client123")).thenReturn(new HashSet<>());
        when(scopeService.generateScope("openid")).thenReturn(Scope.builder().scope("openid").build());

        clientService.authorizeClient(session, "client123", "https://valid.com",
                "code", "openid", "state", "codeChallenge", "S256", "nonce");

        // Verify session attributes were stored correctly
        verify(session).setAttribute("clientId", "client123");
        verify(session).setAttribute("redirectUri", "https://valid.com");
        verify(session).setAttribute("state", "state");
        verify(session).setAttribute("scope", List.of("openid"));
        verify(session).setAttribute("codeChallenge", "codeChallenge");
        verify(session).setAttribute("codeChallengeMethod", "S256");
        verify(session).setAttribute("nonce", "nonce");
    }

    @Test
    void testAuthorizeClient_ValidRequest() {
        when(redirectUriService.isRedirectUriRegisteredByClient("https://valid.com", "client123"))
                .thenReturn(true);
        when(scopeService.isScopeValid("openid")).thenReturn(true);

        HashSet<Scope> allowedScopes = new HashSet<>();
        allowedScopes.add(Scope.builder().scope("openid").build());

        when(userService.getAllowedScopes("client123")).thenReturn(allowedScopes);
        when(scopeService.generateScope("openid")).thenReturn(Scope.builder().scope("openid").build());

        RedirectView redirectView = clientService.authorizeClient(session, "client123", "https://valid.com",
                "code", "openid", "state", "codeChallenge", "S256", "nonce");

        assertTrue(redirectView.getUrl().contains("/client/allow"));
    }

    @Test
    void testSaveClient_CompanyNotFound() {
        ClientRegisterRequestDto dto = ClientRegisterRequestDto.builder()
                .name("clientName")
                .programType(ClientType.SERVER_APP.name())
                .redirectUriList(List.of("https://validurl.com"))
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(redirectUriService.validate("https://validurl.com", ClientType.SERVER_APP)).thenReturn(true);

        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("companyName");
        when(companyRepository.findByUsernameAndEnabledTrue("companyName")).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> clientService.saveClient(dto));
    }

    @Test
    void testSaveClient_Successful() {
        ClientRegisterRequestDto dto = ClientRegisterRequestDto.builder()
                .name("clientName")
                .programType(ClientType.SERVER_APP.name())
                .redirectUriList(List.of("https://validurl.com"))
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(redirectUriService.validate("https://validurl.com", ClientType.SERVER_APP)).thenReturn(true);

        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("companyName");
        when(companyRepository.findByUsernameAndEnabledTrue("companyName")).thenReturn(Optional.of(new Company()));
        when(companyRepository.save(any(Company.class))).thenReturn(new Company());

        ClientRegisterResponseDto responseDto = clientService.saveClient(dto);

        assertNotNull(responseDto);
    }
}
