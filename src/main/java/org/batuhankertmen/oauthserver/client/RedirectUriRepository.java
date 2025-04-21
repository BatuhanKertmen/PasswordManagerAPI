package org.batuhankertmen.oauthserver.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedirectUriRepository extends JpaRepository<RedirectUri, Integer> {
    boolean existsByUriAndClient_ClientId(String uri, String client_id);
    Optional<RedirectUri> findByUri(String uri);
}
