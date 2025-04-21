package org.batuhankertmen.oauthserver.company;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CompanyRepositoryIntegrationTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testSaveAndFindById() {
        Company company = new Company();
        company.setUsername("testCompany");
        company.setEnabled(true);
        company.setPassword("encodedPassword");

        Company savedCompany = companyRepository.save(company);
        Optional<Company> foundCompany = companyRepository.findById(savedCompany.getId());

        assertTrue(foundCompany.isPresent());
        assertEquals("testCompany", foundCompany.get().getUsername());
    }

    @Test
    void testFindByUsernameAndEnabledTrue_CompanyExists() {
        Company company = new Company();
        company.setUsername("activeCompany");
        company.setEnabled(true);
        company.setPassword("encodedPassword");
        companyRepository.save(company);

        Optional<Company> foundCompany = companyRepository.findByUsernameAndEnabledTrue("activeCompany");

        assertTrue(foundCompany.isPresent());
        assertEquals("activeCompany", foundCompany.get().getUsername());
    }

    @Test
    void testFindByUsernameAndEnabledTrue_CompanyNotFound() {
        Optional<Company> foundCompany = companyRepository.findByUsernameAndEnabledTrue("nonExistentCompany");
        assertFalse(foundCompany.isPresent());
    }
}
