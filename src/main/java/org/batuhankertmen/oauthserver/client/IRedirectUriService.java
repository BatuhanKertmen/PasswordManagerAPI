package org.batuhankertmen.oauthserver.client;

public interface IRedirectUriService {

    boolean validate(String redirectUri, ClientType type);

    RedirectUri findByUri(String uri);

    boolean isRedirectUriRegisteredByClient(String uri, String clientId);
}
