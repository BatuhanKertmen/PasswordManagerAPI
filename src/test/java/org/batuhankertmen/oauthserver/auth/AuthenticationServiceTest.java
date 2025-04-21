package org.batuhankertmen.oauthserver.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.batuhankertmen.oauthserver.user.*;
import org.batuhankertmen.oauthserver.common.exception.UserException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegistryRequestDto registryRequestDto;

    @BeforeEach
    void setUp() {
        registryRequestDto = new RegistryRequestDto();
        registryRequestDto.setFirstName("John");
        registryRequestDto.setLastName("Doe");
        registryRequestDto.setUsername("johndoe");
        registryRequestDto.setPassword("password123");
        registryRequestDto.setEmail("john.doe@example.com");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1);
            return user;
        });

        UserDto userDto = authenticationService.register(registryRequestDto, request, response);

        assertNotNull(userDto);
        assertEquals("johndoe", userDto.getUsername());
        assertEquals("John", userDto.getFirstName());
        assertEquals("Doe", userDto.getLastName());
        assertEquals("john.doe@example.com", userDto.getContact());
    }

    @Test
    void testRegister_UsernameExists_ThrowsException() {
        User existingUser = new User();
        existingUser.setUsername(registryRequestDto.getUsername());
        existingUser.setEmail("different@example.com");
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(List.of(existingUser));

        assertThrows(UserException.class, () -> authenticationService.register(registryRequestDto, request, response));
    }

    @Test
    void testRegister_EmailExists_ThrowsException() {
        User existingUser = new User();
        existingUser.setUsername("different");
        existingUser.setEmail(registryRequestDto.getEmail());
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(List.of(existingUser));

        assertThrows(UserException.class, () -> authenticationService.register(registryRequestDto, request, response));
    }
}
