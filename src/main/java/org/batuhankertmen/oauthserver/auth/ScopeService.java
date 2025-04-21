package org.batuhankertmen.oauthserver.auth;

import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.common.exception.AuthException;
import org.batuhankertmen.oauthserver.scope.ScopeEnum;
import org.batuhankertmen.oauthserver.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScopeService implements IScopeService{

    private final ScopeRepository scopeRepository;

    private static final Set<String> VALID_SCOPES = new HashSet<>();
    static {
        for (ScopeEnum scopeEnum : ScopeEnum.values()) {
            VALID_SCOPES.add(scopeEnum.name().toUpperCase(Locale.ENGLISH));
        }
    }

    @Override
    @Transactional
    public void saveAll(Collection<Scope> scopes, User user, Client client) {
        Set<Scope> uniqueScopesToSave = new HashSet<>();

        HashSet<Scope> existingScopes = scopeRepository.findAllByUserAndClient(user, client);

        for (Scope scope : scopes) {
            if (!existingScopes.contains(scope)) {
                user.addScope(scope);
                client.addScope(scope);
                uniqueScopesToSave.add(scope);
            }
        }

        scopeRepository.saveAll(uniqueScopesToSave);
    }

    @Override
    @Transactional(readOnly = true)
    public HashSet<Scope> getAllScopesByUserAndClient(User user, Client client) {
        return scopeRepository.findAllByUserAndClient(user, client);
    }

    @Override
    @Transactional(readOnly = true)
    public HashSet<Scope> getAllScopesByUserAndClientId(User user, String clientId) {
        return scopeRepository.findAllByUserAndClient_ClientId(user, clientId);
    }

    @Override
    public Scope generateScope(String scope) {
        if (!VALID_SCOPES.contains(scope.toUpperCase(Locale.ENGLISH))) {
            throw AuthException.invalidScope();
        }

        return Scope.builder().scope(scope).build();
    }

    @Override
    public boolean isScopeValid(String scope) {
        return VALID_SCOPES.contains(scope.toUpperCase(Locale.ENGLISH));
    }

    @Override
    public Map<String, String> getScopeAndDescriptionPairs(List<String> scopeList) {
        return scopeList.stream()
                .collect(Collectors.toMap(
                        scope -> scope,
                        scope -> ScopeEnum.fromString(scope).getDescription()
                ));
    }

    @Override
    @Transactional
    public void deleteAllScopesByUserAndClient(User user, Client client) {
        Set<Scope> scopes = scopeRepository.findAllByUserAndClient(user, client);

        for (Scope scope : scopes) {
            user.removeScope(scope);
            client.removeScope(scope);
        }
    }
}
