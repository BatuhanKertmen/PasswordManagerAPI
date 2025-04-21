package org.batuhankertmen.oauthserver.client;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.view.RedirectView;

public interface IClientService {
    Client findClientById(String id);

    ClientRegisterResponseDto saveClient(ClientRegisterRequestDto dto);

    boolean checkClientSecret(String clientId, String clientSecret);
    boolean checkClientSecret(Client client, String clientSecret);

    RedirectView authorizeClient(HttpSession session,
                                 String clientId,
                                 String redirectUri,
                                 String responseType,
                                 String scope,
                                 String state,
                                 String codeChallenge,
                                 String codeChallengeMethod,
                                 String nonce);
}
