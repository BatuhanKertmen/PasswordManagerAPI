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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RefreshTokenRepositoryIntegrationTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
    void testSaveAndFindByCode() {
        RefreshToken token = RefreshToken.builder()
                .active(true)
                .code("refresh-123")
                .user(user)
                .client(client)
                .build();
        refreshTokenRepository.save(token);

        Optional<RefreshToken> foundToken = refreshTokenRepository.findByCode("refresh-123");
        assertTrue(foundToken.isPresent());
        assertEquals("refresh-123", foundToken.get().getCode());
    }

    @Test
    void testFindByUserAndClientAndActiveTrue() {
        RefreshToken token = RefreshToken.builder()
                .active(true)
                .code("refresh-456")
                .user(user)
                .client(client)
                .build();
        refreshTokenRepository.save(token);

        List<RefreshToken> foundTokens = refreshTokenRepository.findByUserAndClientAndActiveTrue(user, client);
        assertFalse(foundTokens.isEmpty());
        assertEquals("refresh-456", foundTokens.get(0).getCode());
    }

    @Test
    void testFindByUserAndClientAndActiveTrue_NoActiveTokens() {
        RefreshToken token = RefreshToken.builder()
                .active(false)
                .code("refresh-789")
                .user(user)
                .client(client)
                .build();
        refreshTokenRepository.save(token);

        List<RefreshToken> foundTokens = refreshTokenRepository.findByUserAndClientAndActiveTrue(user, client);
        assertTrue(foundTokens.isEmpty());
    }
}
