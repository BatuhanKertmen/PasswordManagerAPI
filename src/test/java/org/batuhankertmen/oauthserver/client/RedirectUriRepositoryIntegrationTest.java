package org.batuhankertmen.oauthserver.client;

import org.batuhankertmen.oauthserver.company.Company;
import org.batuhankertmen.oauthserver.company.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RedirectUriRepositoryIntegrationTest {

    @Autowired
    private RedirectUriRepository redirectUriRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;
    private Client testClient;

    @BeforeEach
    void setUp() {
        // Create and save a Company
        testCompany = new Company();
        testCompany.setUsername("testCompany");
        testCompany.setPassword("securePassword");
        testCompany.setEnabled(true);
        testCompany = companyRepository.save(testCompany);

        // Create and save a Client linked to the Company
        testClient = Client.builder()
                .clientId("clientId")
                .clientSecret("encodedSecret")
                .clientType(ClientType.SERVER_APP)
                .name("clientName")
                .company(testCompany)
                .build();
        testClient = clientRepository.save(testClient);
    }


    @Test
    void testSaveAndFindById() {
        RedirectUri redirectUri = RedirectUri.builder()
                .uri("https://valid.com")
                .client(testClient)
                .build();

        RedirectUri savedRedirectUri = redirectUriRepository.save(redirectUri);
        Optional<RedirectUri> foundRedirectUri = redirectUriRepository.findById(savedRedirectUri.getId());

        assertTrue(foundRedirectUri.isPresent());
        assertEquals("https://valid.com", foundRedirectUri.get().getUri());
        assertEquals("clientId", foundRedirectUri.get().getClient().getClientId()); // Verify client link
    }

    @Test
    void testFindByUri_UriExists() {
        RedirectUri redirectUri = RedirectUri.builder()
                .uri("https://valid.com")
                .client(testClient)
                .build();
        redirectUriRepository.save(redirectUri);

        Optional<RedirectUri> foundUri = redirectUriRepository.findByUri("https://valid.com");

        assertTrue(foundUri.isPresent());
        assertEquals("https://valid.com", foundUri.get().getUri());
    }

    @Test
    void testFindByUri_UriNotFound() {
        Optional<RedirectUri> foundUri = redirectUriRepository.findByUri("https://notfound.com");
        assertFalse(foundUri.isPresent());
    }

    @Test
    void testExistsByUriAndClient_ClientId_Exists() {
        RedirectUri redirectUri = RedirectUri.builder()
                .uri("https://valid.com")
                .client(testClient)
                .build();
        redirectUriRepository.save(redirectUri);

        boolean exists = redirectUriRepository.existsByUriAndClient_ClientId("https://valid.com", "clientId");
        assertTrue(exists);
    }

    @Test
    void testExistsByUriAndClient_ClientId_NotExists() {
        boolean exists = redirectUriRepository.existsByUriAndClient_ClientId("https://invalid.com", "clientId");
        assertFalse(exists);
    }
}