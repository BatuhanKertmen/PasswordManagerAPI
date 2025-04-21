package org.batuhankertmen.oauthserver.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.common.exception.UserException;
import org.batuhankertmen.oauthserver.user.User;
import org.batuhankertmen.oauthserver.user.UserDto;
import org.batuhankertmen.oauthserver.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto register(RegistryRequestDto registryRequestDto,
                            HttpServletRequest request,
                            HttpServletResponse response
    ) {

        validateUsernameAndEmail(registryRequestDto.getUsername(), registryRequestDto.getEmail());

        User user = createAndSaveUser(
                registryRequestDto.getFirstName(),
                registryRequestDto.getLastName(),
                registryRequestDto.getUsername(),
                registryRequestDto.getPassword(),
                registryRequestDto.getEmail()
        );

        return UserDto.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .contact(user.getEmail())
                .build();
    }

    private void validateUsernameAndEmail(String username, String email) {
        List<User> existingUsers = userRepository.findByUsernameOrEmail(username, email);
        if (!existingUsers.isEmpty()) {
            for (User user : existingUsers) {
                if (user.getUsername().equals(username)) {
                    throw UserException.sameContactAlreadyExists();
                }
                if (user.getEmail().equals(email)) {
                    throw UserException.sameUsernameAlreadyExists();
                }
            }
        }
    }

    private User createAndSaveUser(String firstName, String lastName, String username, String password, String email) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .enabled(true)
                .createdAt(new Date(System.currentTimeMillis()))
                .lastUpdatedAt(new Date(System.currentTimeMillis()))
                .build();


        Authority authority = Authority.builder()
                .role(Role.USER)
                .build();

        user.addAuthority(authority);

        return userRepository.save(user);
    }
}
