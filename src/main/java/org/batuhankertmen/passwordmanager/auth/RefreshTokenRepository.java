package org.batuhankertmen.passwordmanager.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.isValid = false WHERE rt.user.id = :userId")
    void invalidateAllRefreshTokensByUser(int userId);
}
