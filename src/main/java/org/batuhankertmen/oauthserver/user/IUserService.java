package org.batuhankertmen.oauthserver.user;

import org.batuhankertmen.oauthserver.auth.Scope;
import org.batuhankertmen.oauthserver.client.Client;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface IUserService extends UserDetailsService{
    HashSet<Scope> getAllowedScopes(String clientId);

    Map<Client, List<Scope>> getAllowedScopesPerClient();

    void revokeClientPermissions(String clientId);
}
