package org.batuhankertmen.passwordmanager.auth;

import org.batuhankertmen.passwordmanager.user.Role;
import org.batuhankertmen.passwordmanager.user.User;
import org.batuhankertmen.passwordmanager.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest
@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .username("testsaveuser")
                .password("password")
                .enabled(true)
                .firstName("Save")
                .lastName("User")
                .contact("savecontact")
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Test
    public void save() {
        RefreshToken refreshToken = RefreshToken.builder().token("sampletoken").isValid(true).user(user).build();
        refreshTokenRepository.save(refreshToken);

        List<RefreshToken> token_list =  refreshTokenRepository.findAll();

        assertThat(token_list.size()).isEqualTo(1);
        RefreshToken token = token_list.getFirst();

        assertThat(token.getToken()).isEqualTo("sampletoken");
        assertThat(token.isValid()).isTrue();
    }

    @Test
    public void testFindByToken() {
        RefreshToken refreshToken = RefreshToken.builder().token("sampletoken").isValid(true).user(user).build();
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("sampletoken");

        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo("sampletoken");
    }

    @Test
    public void testInvalidateAllRefreshTokensByUser() {
        RefreshToken refreshToken1 = RefreshToken.builder().token("token1").isValid(true).user(user).build();
        RefreshToken refreshToken2 = RefreshToken.builder().token("token2").isValid(true).user(user).build();
        refreshTokenRepository.save(refreshToken1);
        refreshTokenRepository.save(refreshToken2);

        // When
        refreshTokenRepository.invalidateAllRefreshTokensByUser(user.getId());

        // Then
        Optional<RefreshToken> invalidatedToken1 = refreshTokenRepository.findById(refreshToken1.getId());
        Optional<RefreshToken> invalidatedToken2 = refreshTokenRepository.findById(refreshToken2.getId());

        assertThat(invalidatedToken1).isPresent();
        assertThat(invalidatedToken1.get().isValid()).isFalse();
        assertThat(invalidatedToken2).isPresent();
        assertThat(invalidatedToken2.get().isValid()).isFalse();
    }
}
