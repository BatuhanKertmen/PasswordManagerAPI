package org.batuhankertmen.passwordmanager.auth;

import org.batuhankertmen.passwordmanager.common.exception.UserException;
import org.batuhankertmen.passwordmanager.user.Role;
import org.batuhankertmen.passwordmanager.user.User;
import org.batuhankertmen.passwordmanager.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private Authentication authentication;

    @Mock
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccess() {
        RegistryRequestDto request = new RegistryRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setUsername("johndoe");
        request.setPassword("password");
        request.setContact("john@example.com");

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password("encodedPassword")
                .contact(request.getContact())
                .role(Role.USER)
                .enabled(true)
                .createdAt(new Date(System.currentTimeMillis()))
                .lastUpdatedAt(new Date(System.currentTimeMillis()))
                .build();

        when(userRepository.findByUsernameOrContact(request.getUsername(), request.getContact()))
                .thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(refreshTokenService).save(any(String.class), any(User.class));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("jwtToken");

        AuthenticationResponseDto response = authenticationService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwtToken");

        verify(userRepository).findByUsernameOrContact(request.getUsername(), request.getContact());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateAccessToken(any(User.class));
    }

    @Test
    public void testRegisterUserWithExistingUsername() {
        RegistryRequestDto request = new RegistryRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setUsername("johndoe");
        request.setPassword("password");
        request.setContact("john@example.com");

        User existingUser = User.builder()
                .username("johndoe")
                .contact("otherjohn@example.com")
                .build();

        when(userRepository.findByUsernameOrContact(request.getUsername(), request.getContact()))
                .thenReturn(Collections.singletonList(existingUser));
        doNothing().when(refreshTokenService).save(any(String.class), any(User.class));

        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("A user with the same username is already registered.");

        verify(userRepository).findByUsernameOrContact(request.getUsername(), request.getContact());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUserWithExistingContact() {
        RegistryRequestDto request = new RegistryRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setUsername("johndoe");
        request.setPassword("password");
        request.setContact("john@example.com");

        User existingUser = User.builder()
                .username("otheruser")
                .contact("john@example.com")
                .build();

        when(userRepository.findByUsernameOrContact(request.getUsername(), request.getContact()))
                .thenReturn(Collections.singletonList(existingUser));

        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("A user with the same contact information is already registered.");

        verify(userRepository).findByUsernameOrContact(request.getUsername(), request.getContact());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testAuthenticateUserSuccess() {
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setUsername("johndoe");
        request.setPassword("password");

        User user = User.builder()
                .username("johndoe")
                .password("encodedPassword")
                .enabled(true)
                .build();

        when(userRepository.findByUsernameAndEnabledTrue(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("jwtToken");
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        AuthenticationResponseDto response = authenticationService.authenticate(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwtToken");

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameAndEnabledTrue(request.getUsername());
        verify(jwtService).generateAccessToken(user);
    }

    @Test
    public void testAuthenticateUserNotFound() {
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setUsername("johndoe");
        request.setPassword("password");

        when(userRepository.findByUsernameAndEnabledTrue(request.getUsername())).thenReturn(Optional.empty());
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found!");

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameAndEnabledTrue(request.getUsername());
        verify(jwtService, never()).generateAccessToken(any(User.class));
    }
}
