package org.batuhankertmen.oauthserver.auth;

import jakarta.servlet.http.HttpSession;
import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.client.ClientService;
import org.batuhankertmen.oauthserver.client.IRedirectUriService;
import org.batuhankertmen.oauthserver.client.RedirectUri;
import org.batuhankertmen.oauthserver.common.exception.AuthException;
import org.batuhankertmen.oauthserver.common.exception.ClientException;
import org.batuhankertmen.oauthserver.common.exception.RestException;
import org.batuhankertmen.oauthserver.scope.ScopeEnum;
import org.batuhankertmen.oauthserver.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {
    @Mock
    private ClientService clientService;

    @Mock
    private AuthorizationCodeRepository authorizationCodeRepository;

    @Mock
    private IRedirectUriService redirectUriService;

    @Mock
    private IJwtService jwtService;

    @Mock
    private IRefreshTokenService refreshTokenService;

    @Mock
    private IScopeService scopeService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthorizationService authorizationService;

    private HttpSession session;

    private User user = User.builder()
            .username("testUser")
            .email("email@example.com")
            .password("password")
            .enabled(true)
            .refreshTokens(new HashSet<>())
            .build();

    private RedirectUri redirectUri = RedirectUri.builder()
            .uri("https://valid-uri.com")
            .build();

    private Client client = Client.builder()
            .name("clientName")
            .clientId("client123")
            .clientSecret("secret")
            .redirectUriList(Set.of(redirectUri))
            .build();

    private Scope scope = Scope.builder()
            .scope(ScopeEnum.PROFILE.name())
            .build();

    private Scope refreshScope = Scope.builder()
            .scope(ScopeEnum.OFFLINE_ACCESS.name())
            .build();

    private Scope openIdScope = Scope.builder()
            .scope(ScopeEnum.OPENID.name())
            .build();

    private RefreshToken refreshToken = RefreshToken.builder()
            .active(true)
            .code("refresh-code")
            .client(client)
            .user(user)
            .build();

    @BeforeEach
    void setUp() {
        session = mock(HttpSession.class);
    }

    @Test
    void testProcessAuthorization_NoClientInSession_InvalidSession() {
        when(session.getAttribute("state")).thenReturn("state");
        when(session.getAttribute("redirectUri")).thenReturn("uri");
        when(session.getAttribute("clientId")).thenReturn(null);

        assertThrows(RestException.class, () -> authorizationService.processAuthorization(session));
    }

    @Test
    void testProcessAuthorization_NoChallengeMethodInSession_InvalidSession() {
        when(session.getAttribute("state")).thenReturn("state");
        when(session.getAttribute("redirectUri")).thenReturn("uri");

        when(session.getAttribute("clientId")).thenReturn("client123");
        when(clientService.findClientById("client123")).thenReturn(new Client());

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(new User());

        when(session.getAttribute("codeChallengeMethod")).thenReturn(null);

        assertThrows(RestException.class, () -> authorizationService.processAuthorization(session));
    }

    @Test
    void testProcessAuthorization_NoChallengeInSession_InvalidSession() {
        when(session.getAttribute("state")).thenReturn("state");
        when(session.getAttribute("redirectUri")).thenReturn("uri");
        when(session.getAttribute("clientId")).thenReturn("client123");
        when(clientService.findClientById("client123")).thenReturn(new Client());

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(new User());

        when(session.getAttribute("codeChallengeMethod")).thenReturn("S256");
        when(session.getAttribute("codeChallenge")).thenReturn(null);

        assertThrows(RestException.class, () -> authorizationService.processAuthorization(session));
    }

    @Test
    void testProcessAuthorization_NoRedirectUriInSession_InvalidSession() {
        when(session.getAttribute("state")).thenReturn("state");
        when(session.getAttribute("redirectUri")).thenReturn(null);

        assertThrows(RestException.class, () -> authorizationService.processAuthorization(session));
    }

    @Test
    void testProcessAuthorization_CouldNotCreateUniqueCode_InternalError() {
        when(session.getAttribute("state")).thenReturn("state");
        when(session.getAttribute("clientId")).thenReturn("client123");
        when(clientService.findClientById("client123")).thenReturn(new Client());

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(new User());

        when(session.getAttribute("codeChallengeMethod")).thenReturn("S256");
        when(session.getAttribute("codeChallenge")).thenReturn("challenge");
        when(session.getAttribute("redirectUri")).thenReturn("https://valid-url.com");
        when(session.getAttribute("nonce")).thenReturn("nonce");

        when(redirectUriService.findByUri("https://valid-url.com")).thenReturn(new RedirectUri());

        when(authorizationCodeRepository.save(any(AuthorizationCode.class))).thenThrow(DuplicateKeyException.class);

        assertThrows(RestException.class, () -> authorizationService.processAuthorization(session));
    }

    @Test
    void testProcessAuthorization_NoStateInSession_InvalidSession() {
        when(session.getAttribute("state")).thenReturn(null);

        assertThrows(RestException.class, () -> authorizationService.processAuthorization(session));
    }

    @Test
    void testProcessAuthorization_Successful() {
        when(session.getAttribute("state")).thenReturn("state");
        when(session.getAttribute("clientId")).thenReturn("client123");
        when(clientService.findClientById("client123")).thenReturn(new Client());

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(new User());

        when(session.getAttribute("codeChallengeMethod")).thenReturn("S256");
        when(session.getAttribute("codeChallenge")).thenReturn("challenge");
        when(session.getAttribute("redirectUri")).thenReturn("https://valid-url.com");
        when(session.getAttribute("nonce")).thenReturn("nonce");

        when(redirectUriService.findByUri("https://valid-url.com")).thenReturn(RedirectUri.builder().uri("https://valid-url.com").build());
        when(authorizationCodeRepository.save(any(AuthorizationCode.class))).thenReturn(AuthorizationCode.builder().code("code").build());

        when(session.getAttribute("scope")).thenReturn(List.of("openid"));

        when(scopeService.generateScope(any())).thenReturn(Scope.builder().scope("openid").build());
        doNothing().when(scopeService).saveAll(any(), any(User.class), any(Client.class));


        String result = authorizationService.processAuthorization(session);

        Pattern pattern = Pattern.compile("^https://valid-url\\.com\\?code=([a-fA-F0-9]+)&state=state$");
        Matcher matcher = pattern.matcher(result);

        assertTrue(matcher.matches(), "URL does not match expected pattern!");
    }

    @Test
    void testGenerateAccessToken_InvalidGrantType() {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("invalidType");

        assertThrows(ClientException.class, () -> authorizationService.generateAccessToken(dto));
    }

    @Test
    void testGenerateAccessToken_InvalidAuthorizationCode() {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("authorization_code");
        dto.setCode("code");

        when(authorizationCodeRepository.findByCode("code")).thenReturn(Optional.empty());

        assertThrows(ClientException.class, () -> authorizationService.generateAccessToken(dto));
    }

    @Test
    void testGenerateAccessToken_InvalidClientCredentials() {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("authorization_code");
        dto.setClientId("client123");
        dto.setCode("code");
        dto.setClientSecret("secret");

        AuthorizationCode code = AuthorizationCode.builder().code("code").user(user).client(client).build();

        when(authorizationCodeRepository.findByCode("code")).thenReturn(Optional.of(code));
        when(clientService.checkClientSecret(client, dto.getClientSecret())).thenReturn(false);

        assertThrows(ClientException.class, () -> authorizationService.generateAccessToken(dto));
    }

    @Test
    void testGenerateAccessToken_ExpiredCode() {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("authorization_code");
        dto.setClientId("client123");
        dto.setCode("code");
        dto.setClientSecret("secret");
        dto.setRedirectUri("invalid-uri");

        AuthorizationCode code = AuthorizationCode.builder()
                .code("code")
                .user(user)
                .client(client)
                .createdAt(Date.from(Instant.now().minusSeconds(1000 * 60 * 60)))
                .build();

        when(authorizationCodeRepository.findByCode("code")).thenReturn(Optional.of(code));
        when(clientService.checkClientSecret(client, dto.getClientSecret())).thenReturn(true);
        doNothing().when(authorizationCodeRepository).delete(code);

        assertThrows(AuthException.class, () -> authorizationService.generateAccessToken(dto));
    }

    @Test
    void testGenerateAccessToken_UsedCode() {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("authorization_code");
        dto.setClientId("client123");
        dto.setCode("code");
        dto.setClientSecret("secret");
        dto.setRedirectUri(redirectUri.getUri());

        AuthorizationCode code = AuthorizationCode.builder()
                .code("code")
                .user(user)
                .client(client)
                .redirectUri(redirectUri)
                .createdAt(Date.from(Instant.now()))
                .active(false)
                .build();

        when(authorizationCodeRepository.findByCode("code")).thenReturn(Optional.of(code));
        when(clientService.checkClientSecret(client, dto.getClientSecret())).thenReturn(true);
        when(authorizationCodeRepository.findAllByUserAndClient(user, client)).thenReturn(List.of(code));
        when(authorizationCodeRepository.saveAll(List.of(code))).thenReturn(List.of(code));

        assertThrows(AuthException.class, () -> authorizationService.generateAccessToken(dto));
    }

    @Test
    void testGenerateAccessToken_InvalidChallengeCode() {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("authorization_code");
        dto.setClientId("client123");
        dto.setCode("code");
        dto.setClientSecret("secret");
        dto.setCodeVerifier("invalid-verifier");
        dto.setRedirectUri(redirectUri.getUri());

        AuthorizationCode code = AuthorizationCode.builder()
                .code("code")
                .user(user)
                .client(client)
                .redirectUri(redirectUri)
                .createdAt(Date.from(Instant.now()))
                .active(true)
                .codeChallengeMethod(CodeChallengeMethod.PLAIN)
                .codeChallenge("challenge")
                .build();

        when(authorizationCodeRepository.findByCode("code")).thenReturn(Optional.of(code));
        when(clientService.checkClientSecret(client, dto.getClientSecret())).thenReturn(true);

        assertThrows(AuthException.class, () -> authorizationService.generateAccessToken(dto));
    }

    @Test
    void testGenerateAccessToken_OnlyAccessToken() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("authorization_code");
        dto.setClientId("client123");
        dto.setCode("code");
        dto.setClientSecret("secret");
        dto.setCodeVerifier("challenge");
        dto.setRedirectUri(redirectUri.getUri());

        AuthorizationCode code = AuthorizationCode.builder()
                .code("code")
                .user(user)
                .client(client)
                .redirectUri(redirectUri)
                .createdAt(Date.from(Instant.now()))
                .active(true)
                .codeChallengeMethod(CodeChallengeMethod.PLAIN)
                .codeChallenge("challenge")
                .build();

        when(authorizationCodeRepository.findByCode("code")).thenReturn(Optional.of(code));
        when(clientService.checkClientSecret(client, dto.getClientSecret())).thenReturn(true);
        when(authorizationCodeRepository.save(code)).thenReturn(code);

        HashSet<Scope> scopes = new HashSet<>();
        scopes.add(scope);

        when(scopeService.getAllScopesByUserAndClient(user, client)).thenReturn(scopes);
        when(jwtService.generateAccessToken(any(), any(), eq(user), eq(client.getClientId()))).thenReturn("access-token");

        TokenResponseDto result = authorizationService.generateAccessToken(dto);

        assertNotNull(result.getAccessToken());
        assertNull(result.getRefreshToken());
        assertNull(result.getIdToken());
        assertEquals("Bearer", result.getTokenType());
    }

    @Test
    void testGenerateAccessToken_WithRefreshToken() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("authorization_code");
        dto.setClientId("client123");
        dto.setCode("code");
        dto.setClientSecret("secret");
        dto.setCodeVerifier("challenge");
        dto.setRedirectUri(redirectUri.getUri());

        AuthorizationCode code = AuthorizationCode.builder()
                .code("code")
                .user(user)
                .client(client)
                .redirectUri(redirectUri)
                .createdAt(Date.from(Instant.now()))
                .active(true)
                .codeChallengeMethod(CodeChallengeMethod.PLAIN)
                .codeChallenge("challenge")
                .build();

        when(authorizationCodeRepository.findByCode("code")).thenReturn(Optional.of(code));
        when(clientService.checkClientSecret(client, dto.getClientSecret())).thenReturn(true);
        when(authorizationCodeRepository.save(code)).thenReturn(code);

        HashSet<Scope> scopes = new HashSet<>();
        scopes.add(scope);
        scopes.add(refreshScope);

        when(scopeService.getAllScopesByUserAndClient(user, client)).thenReturn(scopes);
        when(jwtService.generateAccessToken(any(), any(), eq(user), eq(client.getClientId()))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken()).thenReturn(refreshToken);
        doNothing().when(refreshTokenService).save(refreshToken);

        TokenResponseDto result = authorizationService.generateAccessToken(dto);

        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        assertNull(result.getIdToken());
        assertEquals("Bearer", result.getTokenType());
    }

    @Test
    void testGenerateAccessToken_WithIdToken() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("authorization_code");
        dto.setClientId("client123");
        dto.setCode("code");
        dto.setClientSecret("secret");
        dto.setCodeVerifier("challenge");
        dto.setRedirectUri(redirectUri.getUri());

        AuthorizationCode code = AuthorizationCode.builder()
                .code("code")
                .user(user)
                .client(client)
                .redirectUri(redirectUri)
                .createdAt(Date.from(Instant.now()))
                .active(true)
                .codeChallengeMethod(CodeChallengeMethod.PLAIN)
                .codeChallenge("challenge")
                .build();

        when(authorizationCodeRepository.findByCode("code")).thenReturn(Optional.of(code));
        when(clientService.checkClientSecret(client, dto.getClientSecret())).thenReturn(true);
        when(authorizationCodeRepository.save(code)).thenReturn(code);

        HashSet<Scope> scopes = new HashSet<>();
        scopes.add(scope);
        scopes.add(openIdScope);

        when(scopeService.getAllScopesByUserAndClient(user, client)).thenReturn(scopes);
        when(jwtService.generateAccessToken(any(), any(), eq(user), eq(client.getClientId()))).thenReturn("access-token");
        when(jwtService.generateIdToken(any(), eq(user), anyString())).thenReturn("id-token");

        TokenResponseDto result = authorizationService.generateAccessToken(dto);

        assertNotNull(result.getAccessToken());
        assertNull(result.getRefreshToken());
        assertNotNull(result.getIdToken());
        assertEquals("Bearer", result.getTokenType());
    }

    @Test
    void testGenerateAccessTokenViaRefreshToken() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setGrantType("refresh_token");
        dto.setClientId("client123");
        dto.setCode("code");
        dto.setClientSecret("secret");
        dto.setCodeVerifier("challenge");
        dto.setRedirectUri(redirectUri.getUri());

        when(refreshTokenService.findByCode(dto.getCode())).thenReturn(refreshToken);
        when(clientService.checkClientSecret(client, dto.getClientSecret())).thenReturn(true);
        doNothing().when(refreshTokenService).disable(refreshToken);

        HashSet<Scope> scopes = new HashSet<>();
        scopes.add(scope);
        scopes.add(refreshScope);

        when(scopeService.getAllScopesByUserAndClient(user, client)).thenReturn(scopes);
        when(jwtService.generateAccessToken(any(), any(), eq(user), eq(client.getClientId()))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken()).thenReturn(refreshToken);

        TokenResponseDto result = authorizationService.generateAccessToken(dto);

        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        assertNull(result.getIdToken());
        assertEquals("Bearer", result.getTokenType());
    }
}
