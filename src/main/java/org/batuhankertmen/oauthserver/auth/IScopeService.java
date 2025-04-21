package org.batuhankertmen.oauthserver.auth;

import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.user.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface IScopeService {

    void saveAll(Collection<Scope> scopes, User user, Client client);

    HashSet<Scope> getAllScopesByUserAndClient(User user, Client client);
    HashSet<Scope> getAllScopesByUserAndClientId(User user, String clientId);

    Scope generateScope(String scope);

    boolean isScopeValid(String scope);

    Map<String, String> getScopeAndDescriptionPairs(List<String> scopeList);

    void deleteAllScopesByUserAndClient(User user, Client client);
}
