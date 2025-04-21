package org.batuhankertmen.oauthserver.auth;

import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, Integer> {

    HashSet<Scope> findAllByUserAndClient(User user, Client client);

    HashSet<Scope> findAllByUserAndClient_ClientId(User user, String clientClientId);
}
