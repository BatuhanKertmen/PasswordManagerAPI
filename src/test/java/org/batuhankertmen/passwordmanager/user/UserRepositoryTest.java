package org.batuhankertmen.passwordmanager.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        // Given
        User user = User.builder()
                .username("testsaveuser")
                .password("password")
                .enabled(true)
                .firstName("Save")
                .lastName("User")
                .contact("savecontact")
                .role(Role.USER)
                .build();

        // When
        User savedUser = userRepository.save(user);

        // Then
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testsaveuser");
        assertThat(foundUser.get().getFirstName()).isEqualTo("Save");
        assertThat(foundUser.get().getLastName()).isEqualTo("User");
        assertThat(foundUser.get().getContact()).isEqualTo("savecontact");
        assertThat(foundUser.get().isEnabled()).isTrue();
    }

    @Test
    void testFindByUsernameAndEnabledTrue() {

        User user = User.builder()
                .username("testuser")
                .password("password")
                .enabled(true)
                .firstName("John")
                .lastName("Doe")
                .contact("testcontact")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsernameAndEnabledTrue("testuser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getId()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testFindByUsernameAndEnabledTrue_UserNotEnabled() {
        User user = User.builder()
                .username("testuser2")
                .password("password")
                .enabled(false)
                .firstName("Jane")
                .lastName("Doe")
                .contact("testcontact2")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsernameAndEnabledTrue("testuser2");

        assertThat(foundUser).isNotPresent();
    }

    @Test
    void testFindByContactAndEnabledTrue() {
        User user = User.builder()
                .username("testuser3")
                .password("password")
                .enabled(true)
                .firstName("Alice")
                .lastName("Smith")
                .contact("testcontact3")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByContactAndEnabledTrue("testcontact3");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getContact()).isEqualTo("testcontact3");
    }

    @Test
    void testFindByContactAndEnabledTrue_UserNotEnabled() {
        User user = User.builder()
                .username("testuser4")
                .password("password")
                .enabled(false)
                .firstName("Bob")
                .lastName("Brown")
                .contact("testcontact4")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByContactAndEnabledTrue("testcontact4");

        assertThat(foundUser).isNotPresent();
    }

    @Test
    void testSaveUserWithSameUsername_ThrowsDataIntegrityViolationException() {
        // Given
        User user1 = User.builder()
                .username("duplicateuser")
                .password("password")
                .enabled(true)
                .firstName("First")
                .lastName("User")
                .contact("contact1")
                .role(Role.USER)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .username("duplicateuser")
                .password("password")
                .enabled(true)
                .firstName("Second")
                .lastName("User")
                .contact("contact2")
                .role(Role.USER)
                .build();

        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user2);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testSaveUserWithSameContact_ThrowsDataIntegrityViolationException() {
        User user1 = User.builder()
                .username("user1")
                .password("password")
                .enabled(true)
                .firstName("First")
                .lastName("User")
                .contact("duplicatecontact")
                .role(Role.USER)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .username("user2")
                .password("password")
                .enabled(true)
                .firstName("Second")
                .lastName("User")
                .contact("duplicatecontact")
                .role(Role.USER)
                .build();

        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user2);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testFindByUsernameOrContact() {
        User user1 = User.builder()
                .username("user1")
                .password("password1")
                .enabled(true)
                .firstName("First1")
                .lastName("User1")
                .contact("contact1")
                .role(Role.USER)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .username("user2")
                .password("password2")
                .enabled(true)
                .firstName("First2")
                .lastName("User2")
                .contact("contact2")
                .role(Role.USER)
                .build();
        userRepository.save(user2);

        User user3 = User.builder()
                .username("user3")
                .password("password3")
                .enabled(true)
                .firstName("First3")
                .lastName("User3")
                .contact("contact3")
                .role(Role.USER)
                .build();
        userRepository.save(user3);


        List<User> foundUsers = userRepository.findByUsernameOrContact("user1", "contact2");


        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting("username").containsExactlyInAnyOrder("user1", "user2");
        assertThat(foundUsers).extracting("contact").containsExactlyInAnyOrder("contact1", "contact2");
    }
}
