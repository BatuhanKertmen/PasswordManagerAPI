package org.batuhankertmen.oauthserver.auth;

import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, Integer> {
    Optional<AuthorizationCode> findByCode(String code);

    List<AuthorizationCode> findAllByUserAndClient(User user, Client client);
}
