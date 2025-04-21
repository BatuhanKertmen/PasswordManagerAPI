package org.batuhankertmen.oauthserver.config;


import org.batuhankertmen.oauthserver.auth.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider userAuthProvider;

    private final AuthenticationProvider companyAuthProvider;

    public SecurityConfig(
            @Qualifier("userAuthProvider") AuthenticationProvider userAuthProvider,
            @Qualifier("companyAuthProvider") AuthenticationProvider companyAuthProvider)
    {
        this.userAuthProvider = userAuthProvider;
        this.companyAuthProvider = companyAuthProvider;
    }

    @Bean
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(
                        "/client/permission",
                        "/client/authorize",
                        "/client/allow",
                        "/auth/perform_login",
                        "/auth/login",
                        "/user/**"
                )
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/perform_login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/login").permitAll()
                        .anyRequest().hasRole(Role.USER.name())
                )
                .authenticationProvider(userAuthProvider)
                .headers( headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin( form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/perform_login")
                        .defaultSuccessUrl("/home")
                        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/auth/login?error=true"))
                        .permitAll());

        return httpSecurity.build();
    }

    @Bean
    public SecurityFilterChain companySecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(
                        "/company/login",
                        "/company/register",
                        "/company/perform_login",
                        "/client/register"
                )
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, "/company/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/company/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/company/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/company/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/company/perform_login").permitAll()
                        .anyRequest().hasRole(Role.COMPANY.name())
                )
                .authenticationProvider(companyAuthProvider)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(form -> form
                        .loginPage("/company/login")
                        .loginProcessingUrl("/company/perform_login")
                        .defaultSuccessUrl("/home")
                        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/company/login?error=true"))
                        .permitAll()
                );

        return httpSecurity.build();
    }
}