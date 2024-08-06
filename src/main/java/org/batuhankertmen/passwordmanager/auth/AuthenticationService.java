package org.batuhankertmen.passwordmanager.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.passwordmanager.common.exception.AuthTokenException;
import org.batuhankertmen.passwordmanager.common.exception.UserException;
import org.batuhankertmen.passwordmanager.user.Role;
import org.batuhankertmen.passwordmanager.user.User;
import org.batuhankertmen.passwordmanager.user.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService{

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authManager;

    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
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

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.save(refreshToken, user);

        return AuthenticationResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.save(refreshToken, user);

        return AuthenticationResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthenticationResponseDto refresh(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            throw AuthTokenException.refreshTokenNotFound();
        }
        final String refreshToken = authHeader.substring(7);
        final String username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = userRepository.findByUsernameAndEnabledTrue(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        if (!jwtService.isJwtValid(refreshToken, user)) {
            throw AuthTokenException.refreshTokenInvalid();
        }


        RefreshToken currentToken = refreshTokenService.getRefreshTokenByToken(refreshToken);

        if (!currentToken.isValid()) {
            refreshTokenService.InvalidateRefreshTokensForUser(user.getId());
            throw AuthTokenException.refreshTokenInvalid();
        }

        currentToken.setValid(false);
        refreshTokenService.save(currentToken);

        String newRefreshToken = jwtService.generateRefreshToken(user);
        refreshTokenService.save(newRefreshToken, user);

        String newAccessToken = jwtService.generateAccessToken(user);
        return new AuthenticationResponseDto(
                newAccessToken,
                newRefreshToken
        );
    }
}
