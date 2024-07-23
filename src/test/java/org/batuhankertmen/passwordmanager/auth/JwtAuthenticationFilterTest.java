package org.batuhankertmen.passwordmanager.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private IJwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtService.extractUsername(anyString())).thenReturn("username");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.isJwtValid(anyString(), any(UserDetails.class))).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtService).extractUsername("validToken");
        verify(userDetailsService).loadUserByUsername("username");
        verify(jwtService).isJwtValid("validToken", userDetails);
        verify(filterChain).doFilter(request, response);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authToken);
    }

    @Test
    public void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtService.extractUsername(anyString())).thenReturn("username");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.isJwtValid(anyString(), any(UserDetails.class))).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtService).extractUsername("invalidToken");
        verify(userDetailsService).loadUserByUsername("username");
        verify(jwtService).isJwtValid("invalidToken", userDetails);
        verify(filterChain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void testDoFilterInternalWithoutAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void testDoFilterInternalWithMalformedAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
