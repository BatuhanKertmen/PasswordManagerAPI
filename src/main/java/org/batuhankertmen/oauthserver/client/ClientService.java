package org.batuhankertmen.oauthserver.client;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.auth.IScopeService;
import org.batuhankertmen.oauthserver.auth.Scope;
import org.batuhankertmen.oauthserver.common.Error;
import org.batuhankertmen.oauthserver.common.exception.AuthException;
import org.batuhankertmen.oauthserver.common.exception.ClientException;
import org.batuhankertmen.oauthserver.common.exception.RestException;
import org.batuhankertmen.oauthserver.common.exception.UserException;
import org.batuhankertmen.oauthserver.company.Company;
import org.batuhankertmen.oauthserver.company.CompanyRepository;
import org.batuhankertmen.oauthserver.scope.ScopeEnum;
import org.batuhankertmen.oauthserver.user.IUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.batuhankertmen.oauthserver.auth.JwtService.createHexadecimalEncodedRandomBytes;

@Service
@RequiredArgsConstructor
public class ClientService implements IClientService{

    private final ClientRepository clientRepository;

    private final IRedirectUriService redirectUriService;

    private final IUserService userService;

    private final IScopeService scopeService;

    private final PasswordEncoder passwordEncoder;

    private final CompanyRepository companyRepository;



    @Override
    public Client findClientById(String id) {
        return clientRepository.findById(id).orElseThrow(() ->
                ClientException.clientNotFoundException("Client with id " + id + " not found")
        );
    }

    @Override
    @Transactional
    public ClientRegisterResponseDto saveClient(ClientRegisterRequestDto dto) {
        String clientSecret = createHexadecimalEncodedRandomBytes(32);
        Client client = createClient(clientSecret, dto.getName(), dto.getProgramType());
        
        saveRedirectUriList(dto.getRedirectUriList(), client);

        saveClientToCompany(client);

        return buildClientRegistryResponse(client, clientSecret);
    }

    private Client createClient(String clientSecret, String name, String programType) {
        // client password is bcrypt hashed
        String  clientId = "oauth-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return Client.builder()
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(clientSecret))
                .name(name)
                .clientType(ClientType.valueOf(programType))
                .build();
    }

    private void saveRedirectUriList(List<String> redirectUriList, Client client) {
        for (String uri : redirectUriList) {
            if (redirectUriService.validate(uri, client.getClientType()))
            {
                RedirectUri redirectUri = RedirectUri.builder().uri(uri).build();
                client.addRedirectUri(redirectUri);
            }
        }
    }

    private void saveClientToCompany(Client client) {
        String authenticatedCompany = SecurityContextHolder.getContext().getAuthentication().getName();
        Company persistedCompany = companyRepository.findByUsernameAndEnabledTrue(authenticatedCompany)
                .orElseThrow(UserException::userNotFound);

        persistedCompany.addClient(client);

        companyRepository.save(persistedCompany);
    }

    private ClientRegisterResponseDto buildClientRegistryResponse(Client client, String clientSecret) {
        return ClientRegisterResponseDto.builder()
                .clientId(client.getClientId())
                .clientSecret(clientSecret)
                .build();
    }


    @Override
    public boolean checkClientSecret(String clientId, String clientSecret) {
        Client client = clientRepository.findById(clientId).orElseThrow(() ->
                ClientException.clientNotFoundException("Client with id " + clientId + " not found")
        );

        return checkClientSecret(client, clientSecret);
    }

    @Override
    public boolean checkClientSecret(Client client, String clientSecret) {
        return passwordEncoder.matches(clientSecret, client.getClientSecret());
    }

    @Override
    @Transactional
    public RedirectView authorizeClient(
            HttpSession session,
            String clientId,
            String redirectUri,
            String responseType,
            String scope,
            String state,
            String codeChallenge,
            String codeChallengeMethod,
            String nonce
    ) {
        String urlDecodedRedirectUri = URLDecoder.decode(redirectUri, StandardCharsets.UTF_8);
        validateRedirectUri(clientId, urlDecodedRedirectUri);

        if (!Objects.equals(responseType, "code")) {
            return handleInvalidResponseType(urlDecodedRedirectUri, state);
        }

        validateNonce(scope, nonce);

        List<String> scopeList = processScopes(scope);

        storeSessionAttributes(session, clientId, urlDecodedRedirectUri, state, scopeList, codeChallenge, codeChallengeMethod, nonce);
        List<Scope> notAllowedScopes = extractNotAllowedScopes(clientId, scopeList);

        return createRedirectionLink(notAllowedScopes, redirectUri);
    }

    private void validateRedirectUri(String clientId, String redirectUri) {
        if (!redirectUriService.isRedirectUriRegisteredByClient(redirectUri, clientId)) {
            throw ClientException.redirectUriNotRegistered();
        }
    }

    private RedirectView handleInvalidResponseType(String redirectUri, String state) {
        String redirectLink = UriComponentsBuilder
                .fromHttpUrl(redirectUri)
                .queryParam("error", "invalid_request")
                .queryParam("error_description", URLEncoder.encode(ClientException.invalidResponseType().getMessage(), StandardCharsets.UTF_8))
                .queryParam("state", state)
                .toUriString();

        return new RedirectView(redirectLink);
    }

    private void validateNonce(String scope, String nonce) {
        if (scope.toLowerCase().contains(ScopeEnum.OPENID.name().toLowerCase(Locale.ENGLISH))) {
            if (nonce == null) {
                throw RestException.badRequest(
                        Error.INVALID_REQUEST,
                        "openid scope included but nonce not provided!"
                );
            }
        }
    }

    List<String> processScopes(String scope) {
        List<String> scopeList = new ArrayList<>();

        for (String scopeStr : scope.split(" ")) {
            if (!scopeStr.isBlank()) {
                if (!scopeService.isScopeValid(scopeStr)) {
                    throw AuthException.invalidScope();
                }
                scopeList.add(scopeStr);
            }
        }

        return scopeList;
    }

    private void storeSessionAttributes(
            HttpSession session,
            String  clientId,
            String redirectUri,
            String state,
            List<String> scopeList,
            String codeChallenge,
            String codeChallengeMethod,
            String nonce
    ) {
        session.setAttribute("clientId", clientId);
        session.setAttribute("redirectUri", redirectUri);
        session.setAttribute("state", state);
        session.setAttribute("scope", scopeList);
        session.setAttribute("codeChallenge", codeChallenge);
        session.setAttribute("codeChallengeMethod", codeChallengeMethod);
        if (nonce != null) {
            session.setAttribute("nonce", nonce);
        }
    }

    private List<Scope> extractNotAllowedScopes(String clientId, List<String> scopeList) {
        HashSet<Scope> currentAllowedScopes = userService.getAllowedScopes(clientId);

        List<Scope> notAllowedScopes = new ArrayList<>();
        for (String scopeStr : scopeList) {
            Scope tempScope = scopeService.generateScope(scopeStr);
            if (!currentAllowedScopes.contains(tempScope)) {
                notAllowedScopes.add(tempScope);
            }
        }

        return notAllowedScopes;
    }

    private RedirectView createRedirectionLink(List<Scope> notAllowedScopes, String redirectUri) {
        // if there are not allowed scopes ask for permission
        if (!notAllowedScopes.isEmpty()) {
            String scopeQueryString = notAllowedScopes.stream().map(Scope::getScope).collect(Collectors.joining(","));

            String redirectUrl = UriComponentsBuilder
                    .fromPath("permission")
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("scopes", scopeQueryString)
                    .toUriString();

            return new RedirectView(redirectUrl);
        }

        // if allowed, generate authorization code
        return new RedirectView("/client/allow");
    }

}

