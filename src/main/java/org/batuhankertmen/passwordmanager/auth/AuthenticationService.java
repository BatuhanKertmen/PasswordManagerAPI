package org.batuhankertmen.passwordmanager.auth;

import lombok.RequiredArgsConstructor;
import org.batuhankertmen.passwordmanager.common.UserException;
import org.batuhankertmen.passwordmanager.user.Role;
import org.batuhankertmen.passwordmanager.user.User;
import org.batuhankertmen.passwordmanager.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService{

    private final    JwtService jwtService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authManager;

    @Override
    public AuthenticationResponseDto register(RegistryRequestDto request) {
        List<User> existingUsers = userRepository.findByUsernameOrContact(request.getUsername(), request.getContact());
        if (!existingUsers.isEmpty()) {
            for (User user : existingUsers) {
                if (user.getContact().equals(request.getContact())) {
                    throw UserException.sameContactAlreadyExists();
                }
                if (user.getUsername().equals(request.getUsername())) {
                    throw UserException.sameUsernameAlreadyExists();
                }
            }
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .contact(request.getContact())
                .role(Role.USER)
                .enabled(true)
                .createdAt(new Date(System.currentTimeMillis()))
                .lastUpdatedAt(new Date(System.currentTimeMillis()))
                .build();


        userRepository.save(user);

        String jwt = jwtService.generateJwtToken(user);
        return AuthenticationResponseDto.builder()
                .jwt(jwt)
                .build();

    }

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsernameAndEnabledTrue(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        String jwt = jwtService.generateJwtToken(user);
        return AuthenticationResponseDto.builder()
                .jwt(jwt)
                .build();
    }
}
