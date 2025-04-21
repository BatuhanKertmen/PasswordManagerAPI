package org.batuhankertmen.oauthserver.auth;

import org.batuhankertmen.oauthserver.client.*;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AuthorizationCodeRepositoryIntegrationTest {

    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private User user;
    private Company company;
    private Client client;
    private RedirectUri redirectUri;
    private AuthorizationCode code;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .username("testUser")
                .email("email@example.com")
                .password("password")
                .enabled(true)
                .build() ;
        user = userRepository.save(user);

        company = Company.builder()
                .username("testCompany")
                .enabled(true)
                .password("password")
                .build();


        client = Client.builder()
                .clientId("client123")
                .clientSecret("password")
                .name("clientName")
                .clientType(ClientType.SERVER_APP)
                .company(company)
                .build();
        company.addClient(client);


        redirectUri = RedirectUri.builder()
                .uri("https://valid-url.com")
                .client(client)
                .build();
        client.addRedirectUri(redirectUri);
        companyRepository.save(company);

        code = AuthorizationCode.builder()
                .code("code")
                .codeChallenge("challenge")
                .codeChallengeMethod(CodeChallengeMethod.PLAIN)
                .user(user)
                .client(client)
                .createdAt(Date.from(Instant.now()))
                .redirectUri(redirectUri)
                .build();
        code.addUserAndClient(user, client);
        code = authorizationCodeRepository.save(code);
    }

    @Test
    void testFindByCode_CodeExists() {
        Optional<AuthorizationCode> result = authorizationCodeRepository.findByCode(code.getCode());

        assertTrue(result.isPresent());
        assertEquals(code.getCode(), result.get().getCode());
    }

    @Test
    void testFindByCode_CodeNotExists() {
        Optional<AuthorizationCode> result = authorizationCodeRepository.findByCode("invalid-code");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllByUserAndClient() {
        List<AuthorizationCode> codes = authorizationCodeRepository.findAllByUserAndClient(user, client);

        assertEquals(1, codes.size());
        assertEquals(code.getCode(), codes.get(0).getCode());
    }

}
