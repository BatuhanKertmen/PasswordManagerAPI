package org.batuhankertmen.oauthserver.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.client.*;
import org.batuhankertmen.oauthserver.common.exception.AuthException;
import org.batuhankertmen.oauthserver.common.exception.ClientException;
import org.batuhankertmen.oauthserver.common.exception.RestException;
import org.batuhankertmen.oauthserver.scope.ScopeEnum;
import org.batuhankertmen.oauthserver.user.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.batuhankertmen.oauthserver.auth.JwtService.createHexadecimalEncodedRandomBytes;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements IAuthorizationService{

    private final ClientService clientService;

    private final AuthorizationCodeRepository authorizationCodeRepository;

    private final IRedirectUriService redirectUriService;

    private final IJwtService jwtService;

    private final IRefreshTokenService refreshTokenService;

    private final IScopeService scopeService;

    private final int MAX_ATTEMPT = 5;

    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public String processAuthorization(HttpSession session) {
        String state = getStateFromSession(session);
        session.removeAttribute("state");

        RedirectUri redirectUri = getRedirectUriFromSession(session);
        session.removeAttribute("redirectUri");

        String code = this.generateAuthorizationCode(session, redirectUri);

        return UriComponentsBuilder.fromHttpUrl(redirectUri.getUri())
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();
    }


    private String generateAuthorizationCode(HttpSession session, RedirectUri redirectUri) {
        Client client = getClientFromSession(session);
        User user = getUserFromSecurityContext();
        CodeChallengeMethod codeChallengeMethod = getCodeChallengeMethodFromSession(session);
        String codeChallenge = getCodeChallengeFromSession(session);

        AuthorizationCode authorizationCode = createAndSaveAuthorizationCode(session,
                                                    user,
                                                    client,
                                                    codeChallengeMethod,
                                                    codeChallenge,
                                                    redirectUri);

        saveScopesFromSession(session, user, client);

        return authorizationCode.getCode();
    }

    private String getStateFromSession(HttpSession session) {
        String state = (String) session.getAttribute("state");
        if (state == null) {
            throw RestException.invalidSessionException();
        }

        return state;
    }

    private Client getClientFromSession(HttpSession session) {
        String clientId = (String) session.getAttribute("clientId");
        session.removeAttribute("clientId");
        if (clientId == null) {
            throw RestException.invalidSessionException();
        }
        return clientService.findClientById(clientId);
    }

    private User getUserFromSecurityContext() {
        return (User) userDetailsService.loadUserByUsername(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );
    }

    private String getCodeChallengeFromSession(HttpSession session) {
        String codeChallenge = (String) session.getAttribute("codeChallenge");
        if (codeChallenge == null) {
            throw AuthException.codeChallengeMissing();
        }
        return codeChallenge;
    }

    private RedirectUri getRedirectUriFromSession(HttpSession session) {
        String uri = (String) session.getAttribute("redirectUri");
        if (uri == null) {
            throw RestException.invalidSessionException();
        }
        return redirectUriService.findByUri(uri);
    }

    private AuthorizationCode createAndSaveAuthorizationCode(HttpSession session,
                                                             User user,
                                                             Client client,
                                                             CodeChallengeMethod codeChallengeMethod,
                                                             String codeChallenge,
                                                             RedirectUri redirectUri)
    {
        int attempt = 0;
        boolean isUnique = false;
        AuthorizationCode code = AuthorizationCode.builder()
                .createdAt(Date.from(Instant.now()))
                .active(true)
                .codeChallengeMethod(codeChallengeMethod)
                .codeChallenge(codeChallenge)
                .nonce((String) session.getAttribute("nonce"))
                .build();
        code.addUserAndClient(user, client);
        redirectUri.addAuthorizationCode(code);

        session.removeAttribute("nonce");

        // create a unique authorization code
        // max 5 tries
        while (!isUnique && attempt < MAX_ATTEMPT) {
            code.setCode(createHexadecimalEncodedRandomBytes(32));

            try {
                authorizationCodeRepository.save(code);
                isUnique = true;
            } catch (DuplicateKeyException e) {
                attempt++;
            }
        }

        if (!isUnique) {
            throw RestException.internalServerError("Authorization Code could not be created!");
        }

        return code;
    }

    private void saveScopesFromSession(HttpSession session, User user, Client client) {
        List<Scope> scopeList = new ArrayList<>();

        List<String> scopes = (List<String>) session.getAttribute("scope");
        session.removeAttribute("scope");
        if (scopes == null) {
            throw RestException.invalidSessionException();
        }

        for (String scopeStr : scopes) {
            scopeList.add(scopeService.generateScope(scopeStr));
        }

        scopeService.saveAll(scopeList, user, client);
    }

    private CodeChallengeMethod getCodeChallengeMethodFromSession(HttpSession session) {
        String codeChallengeMethodString = (String) session.getAttribute("codeChallengeMethod");
        if (codeChallengeMethodString == null) {
            throw AuthException.invalidCodeChallengeMethod();
        }
        return CodeChallengeMethod.fromString(codeChallengeMethodString);
    }

    @Transactional
    public TokenResponseDto generateAccessToken(TokenRequestDto dto)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        if ("authorization_code".equals(dto.getGrantType())) {
            return generateTokensFromAuthorizationCode(dto);
        }
        else if ("refresh_token".equals(dto.getGrantType())) {
            return generateTokensFromRefreshToken(dto);
        }

        throw ClientException.invalidGrantType();
    }

    private Map<String, Object> generateClaims(User user, Client client) {
        Set<Scope> scopes = scopeService.getAllScopesByUserAndClient(user, client);
        Set<String> scopesStr = scopes.stream().map(scope -> scope.getScope().toLowerCase(Locale.ENGLISH)).collect(Collectors.toSet());

        return new HashMap<>(Map.of(
                "scope", scopesStr,
                "client_id", client.getClientId()
        ));
    }

    private TokenResponseDto generateTokensFromAuthorizationCode(TokenRequestDto dto)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        AuthorizationCode authorizationCode = validateAuthorizationCode(dto);
        User user = authorizationCode.getUser();
        Client client = authorizationCode.getClient();

        validateClientCredentials(client, dto);
        validateAuthorizationCodeExpiry(authorizationCode);
        validateRedirectUri(authorizationCode, dto);
        validateReplayAttack(authorizationCode, user, client);
        validateCodeVerifier(authorizationCode, dto.getCodeVerifier());

        authorizationCode.setActive(false);
        authorizationCodeRepository.save(authorizationCode);

        return generateTokenResponse(user, client, authorizationCode.getNonce());
    }

    private TokenResponseDto generateTokensFromRefreshToken(TokenRequestDto dto)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        RefreshToken refreshToken = refreshTokenService.findByCode(dto.getCode());
        Client client = refreshToken.getClient();

        // check client id
        validateClientCredentials(client, dto);

        // disable existing token
        refreshTokenService.disable(refreshToken);

        return generateTokenResponse(refreshToken.getUser(), client, null);
    }

    private TokenResponseDto generateTokenResponse(User user, Client client, String nonce)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // generate claims
        Map<String, Object> claims = generateClaims(user, client);
        Map<String, Object> headerClaims = Map.of("typ", "at+jwt");

        // generate tokens
        HashSet<String> claimsSet = (HashSet<String>) claims.get("scope");

        String accessToken = jwtService.generateAccessToken(claims, headerClaims, user, client.getClientId());
        RefreshToken newRefreshToken = claimsSet.contains(ScopeEnum.OFFLINE_ACCESS.name().toLowerCase(Locale.ENGLISH))
                ? generateRefreshToken(user, client)
                : null;

        String idToken = claimsSet.contains(ScopeEnum.OPENID.name().toLowerCase(Locale.ENGLISH))
                ? generateIdToken(claimsSet, user, nonce, client.getClientId())
                : null;

        return buildAccessTokenResponse(accessToken, newRefreshToken, idToken, "Bearer", jwtService.getExpirationDate());
    }

    private AuthorizationCode validateAuthorizationCode(TokenRequestDto dto) {
        return authorizationCodeRepository.findByCode(dto.getCode())
                .orElseThrow(ClientException::invalidAuthorizationCode);
    }

    private void validateClientCredentials(Client client, TokenRequestDto dto) {
        if (!Objects.equals(client.getClientId(), dto.getClientId()) ||
                !clientService.checkClientSecret(client, dto.getClientSecret())) {
            throw ClientException.invalidCredentials();
        }
    }

    private void validateAuthorizationCodeExpiry(AuthorizationCode authorizationCode) {
        long expiryTime = 1000 * 60;
        if (Date.from(Instant.now()).getTime() - authorizationCode.getCreatedAt().getTime() > expiryTime) {
            authorizationCodeRepository.delete(authorizationCode);
            throw AuthException.authorizationCodeExpired();
        }
    }

    private void validateRedirectUri(AuthorizationCode authorizationCode, TokenRequestDto dto) {
        if (!authorizationCode.getRedirectUri().getUri().equals(dto.getRedirectUri())) {
            throw ClientException.incorrectRedirectUri();
        }
    }

    private void validateReplayAttack(AuthorizationCode authorizationCode, User user, Client client) {
        if (!authorizationCode.isActive()) {
            List<AuthorizationCode> clientUserCodePairs = authorizationCodeRepository.findAllByUserAndClient(user, client);
            clientUserCodePairs.forEach(code -> code.setActive(false));
            authorizationCodeRepository.saveAll(clientUserCodePairs);
            throw AuthException.authorizationCodeExpired();
        }
    }

    private void validateCodeVerifier(AuthorizationCode authorizationCode, String codeVerifier)
            throws NoSuchAlgorithmException {
        switch (authorizationCode.getCodeChallengeMethod()) {
            case S256:
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String hashedCodeVerifier = Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(digest.digest(codeVerifier.getBytes()));

                if (!Objects.equals(hashedCodeVerifier, authorizationCode.getCodeChallenge())) {
                    throw AuthException.invalidGrant();
                }
                break;
            case PLAIN:
                if (!Objects.equals(codeVerifier, authorizationCode.getCodeChallenge())) {
                    throw AuthException.invalidGrant();
                }
                break;
            default:
                throw AuthException.invalidCodeChallengeMethod();
        }
    }

    private RefreshToken generateRefreshToken(User user, Client client) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken();
        user.addRefreshToken(refreshToken);
        client.addRefreshToken(refreshToken);
        refreshTokenService.save(refreshToken);
        return refreshToken;
    }

    private TokenResponseDto buildAccessTokenResponse(String accessToken,
                                                      RefreshToken refreshToken,
                                                      String idToken,
                                                      String bearerType,
                                                      String expirationDate
    ) {
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .tokenType(bearerType)
                .expires(expirationDate)
                .refreshToken(refreshToken != null ? refreshToken.getCode() : null)
                .idToken(idToken)
                .build();
    }

    private String generateIdToken(HashSet<String> scopes, User user, String nonce, String audience)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        HashMap<String, Object> claims = new HashMap<>();

        if (scopes.contains(ScopeEnum.PROFILE.name().toLowerCase(Locale.ENGLISH))) {
            claims.put("username", user.getUsername());
            claims.put("firstname", user.getFirstName());
            claims.put("middlename", user.getMiddleName());
            claims.put("lastname", user.getLastName());
            claims.put("birthday", user.getBirthday());
            claims.put("profile_pic", user.getProfilePicture());
        }

        if (scopes.contains(ScopeEnum.ADDRESS.name().toLowerCase(Locale.ENGLISH))) {
            claims.put("address", user.getAddress());
            claims.put("locale", user.getLocale());
        }

        if (scopes.contains(ScopeEnum.EMAIL.name().toLowerCase(Locale.ENGLISH))) {
            claims.put("email", user.getEmail());
            claims.put("email_verified", user.isEmailVerified());
        }

        if (nonce != null) {
            return jwtService.generateIdToken(claims, user, nonce);
        }
        return jwtService.generateIdToken(claims, user, audience);
    }
}
