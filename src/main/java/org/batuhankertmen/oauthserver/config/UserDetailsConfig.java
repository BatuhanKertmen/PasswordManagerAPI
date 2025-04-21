package org.batuhankertmen.oauthserver.config;

import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.company.ICompanyService;
import org.batuhankertmen.oauthserver.user.IUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class UserDetailsConfig {

    private final ICompanyService companyService;
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Qualifier("userAuthProvider")
    public AuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    @Qualifier("companyAuthProvider")
    public AuthenticationProvider companyAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(companyService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }


}
