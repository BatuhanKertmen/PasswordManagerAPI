package org.batuhankertmen.oauthserver.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndEnabledTrue(String username);
    List<User> findByUsernameOrEmail(String username, String email);
}
