package org.batuhankertmen.passwordmanager.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndEnabledTrue(String username);
    Optional<User> findByContactAndEnabledTrue(String username);
}
