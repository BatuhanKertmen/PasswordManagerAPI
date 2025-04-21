package org.batuhankertmen.oauthserver.common.exception;


import org.batuhankertmen.oauthserver.common.Error;
import org.batuhankertmen.oauthserver.common.RestResponse;

public class ClientException extends RestException {


    protected ClientException(RestResponse restResponse) {
        super(restResponse);
    }

    public static ClientException clientNotFoundException() {
        return clientNotFoundException("Client not found");
    }

    public static ClientException clientNotFoundException(String message) {
        return new ClientException(RestResponse.unauthorized(Error.INVALID_CLIENT, message));
    }

    public static ClientException redirectUriNotRegistered() {
        return redirectUriNotRegistered("This redirect URI is invalid!");
    }

    public static ClientException redirectUriNotRegistered(String message) {
        return new ClientException(RestResponse.badRequest(Error.INVALID_REQUEST, message));
    }

    public static ClientException incorrectRedirectUri() {
        return new ClientException(RestResponse.badRequest(Error.INVALID_REQUEST,"Incorrect redirect_uri provided!"));
    }

    public static ClientException invalidRedirectUri() {
        return new ClientException(RestResponse.badRequest(Error.INVALID_REQUEST, "Redirect uri can not contain fragments!"));
    }

    public static ClientException invalidResponseType() {
        return new ClientException( RestResponse.badRequest(Error.INVALID_GRANT,"Response type is invalid!"));
    }

    public static ClientException invalidGrantType() {
        return new ClientException( RestResponse.badRequest(Error.INVALID_GRANT,"Response grant type is invalid!"));
    }

    public static ClientException invalidCredentials() {
        return new ClientException( RestResponse.unauthorized(Error.INVALID_CLIENT,"Invalid credentials"));
    }

    public static ClientException invalidAuthorizationCode() {
        return new ClientException(RestResponse.forbidden(Error.INVALID_REQUEST,"Invalid authorization code!"));
    }

}
