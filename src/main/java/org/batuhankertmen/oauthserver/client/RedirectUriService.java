package org.batuhankertmen.oauthserver.client;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedirectUriService implements IRedirectUriService{

    private final RedirectUriRepository redirectUriRepository;

    // validate and save url
    public boolean validate(String uri, ClientType type) {
        if (uri.contains("#")) {
           return false;
        }

        if (type == ClientType.SERVER_APP || type == ClientType.SINGLE_PAGE_APP) {
            if (!uri.startsWith("http")) {
                return false;
            }

            if (!uri.startsWith("https") && !uri.startsWith("http://localhost")) {
                return false;
            }
        }

        return true;
    }

    @Override
    public RedirectUri findByUri(String uri) {
        return redirectUriRepository.findByUri(uri).orElseThrow(() ->
                new EntityNotFoundException("RedirectUri not found!")
        );
    }

    @Override
    public boolean isRedirectUriRegisteredByClient(String uri, String clientId) {
        return redirectUriRepository.existsByUriAndClient_ClientId(uri, clientId);
    }
}
