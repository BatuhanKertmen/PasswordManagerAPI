package org.batuhankertmen.oauthserver.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        User user = User.builder()
                .username("testUser")
                .password("password")
                .enabled(true)
                .email("test@example.com")
                .build();

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsernameAndEnabledTrue_UserExists() {
        User user = User.builder()
                .username("activeUser")
                .password("password")
                .enabled(true)
                .email("active@example.com")
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsernameAndEnabledTrue("activeUser");

        assertTrue(foundUser.isPresent());
        assertEquals("activeUser", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsernameAndEnabledTrue_UserNotFound() {
        Optional<User> foundUser = userRepository.findByUsernameAndEnabledTrue("nonExistentUser");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsernameOrEmail() {
        User user1 = User.builder()
                .username("user1")
                .password("password1")
                .enabled(true)
                .email("user1@example.com")
                .build();
        User user2 = User.builder()
                .username("user2")
                .password("password2")
                .enabled(false)
                .email("user2@example.com")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findByUsernameOrEmail("user1", "user2@example.com");

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
    }
}
