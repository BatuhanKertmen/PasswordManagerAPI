package org.batuhankertmen.oauthserver.company;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void testLoadUserByUsername_CompanyExists() {
        Company company = new Company();
        company.setUsername("testCompany");
        company.setEnabled(true);

        when(companyRepository.findByUsernameAndEnabledTrue("testCompany")).thenReturn(Optional.of(company));

        UserDetails userDetails = companyService.loadUserByUsername("testCompany");
        assertNotNull(userDetails);
        assertEquals("testCompany", userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_CompanyNotFound() {
        when(companyRepository.findByUsernameAndEnabledTrue("unknownCompany")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> companyService.loadUserByUsername("unknownCompany"));
    }

    @Test
    void testSaveCompany_Success() {
        CompanyRegisterDto dto = CompanyRegisterDto.builder()
                        .username("newCompany")
                        .password("securePassword")
                        .build();

        when(passwordEncoder.encode("securePassword")).thenReturn("encodedPassword");

        companyService.save(dto);

        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository, times(1)).save(companyCaptor.capture());

        Company savedCompany = companyCaptor.getValue();
        assertEquals("newCompany", savedCompany.getUsername());
        assertEquals("encodedPassword", savedCompany.getPassword());
    }
}
