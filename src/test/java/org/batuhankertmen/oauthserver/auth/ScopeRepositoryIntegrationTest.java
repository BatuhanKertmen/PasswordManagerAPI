package org.batuhankertmen.oauthserver.auth;

import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.client.ClientRepository;
import org.batuhankertmen.oauthserver.client.ClientType;
import org.batuhankertmen.oauthserver.company.Company;
import org.batuhankertmen.oauthserver.company.CompanyRepository;
import org.batuhankertmen.oauthserver.user.User;
import org.batuhankertmen.oauthserver.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ScopeRepositoryIntegrationTest {

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private User user;
    private Client client;
    private Company company;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setEnabled(true);
        user.setPassword("123");
        user.setEmail("email@example.com");
        user = userRepository.save(user);

        // Create and save a Company
        company = new Company();
        company.setUsername("testCompany");
        company.setPassword("securePassword");
        company.setEnabled(true);
        company = companyRepository.save(company);

        client = new Client();
        client.setClientId("client123");
        client.setClientSecret("123");
        client.setName("clientName");
        client.setClientType(ClientType.SERVER_APP);
        client.setCompany(company);
        client = clientRepository.save(client);
    }

    @Test
    void testSaveAndFindByUserAndClient() {
        Scope scope = new Scope();
        scope.setScope("openid");
        scope.setUser(user);
        scope.setClient(client);
        scopeRepository.save(scope);

        HashSet<Scope> foundScopes = scopeRepository.findAllByUserAndClient(user, client);
        assertEquals(1, foundScopes.size());
        assertEquals("openid", foundScopes.iterator().next().getScope());
    }

    @Test
    void testFindAllByUserAndClientId() {
        Scope scope = new Scope();
        scope.setScope("profile");
        scope.setUser(user);
        scope.setClient(client);
        scopeRepository.save(scope);

        HashSet<Scope> foundScopes = scopeRepository.findAllByUserAndClient_ClientId(user, "client123");
        assertEquals(1, foundScopes.size());
        assertEquals("profile", foundScopes.iterator().next().getScope());
    }

    @Test
    void testFindAllByUserAndClient_NoScopesFound() {
        HashSet<Scope> foundScopes = scopeRepository.findAllByUserAndClient(user, client);
        assertTrue(foundScopes.isEmpty());
    }

    @Test
    void testFindAllByUserAndClient_ClientId_NoScopesFound() {
        HashSet<Scope> foundScopes = scopeRepository.findAllByUserAndClient_ClientId(user, "client123");
        assertTrue(foundScopes.isEmpty());
    }
}
