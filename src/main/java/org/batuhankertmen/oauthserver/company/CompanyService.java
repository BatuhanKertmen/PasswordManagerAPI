package org.batuhankertmen.oauthserver.company;

import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.auth.Authority;
import org.batuhankertmen.oauthserver.auth.Role;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CompanyService implements ICompanyService{

    private final CompanyRepository companyRepository;

    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return companyRepository.findByUsernameAndEnabledTrue(username).orElseThrow(() ->
                new UsernameNotFoundException("Company not found!")
        );
    }

    @Override
    @Transactional
    public void save(CompanyRegisterDto dto) {
        Company company = Company.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .enabled(true)
                .createdAt(Date.from(Instant.now()))
                .lastUpdatedAt(Date.from(Instant.now()))
                .build();

        Authority authority = Authority.builder()
                .role(Role.COMPANY)
                .build();

        company.addAuthority(authority);

        companyRepository.save(company);
    }
}
