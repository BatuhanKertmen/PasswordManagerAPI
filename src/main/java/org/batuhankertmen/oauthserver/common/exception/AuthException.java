package org.batuhankertmen.oauthserver.common.exception;


import org.batuhankertmen.oauthserver.common.Error;
import org.batuhankertmen.oauthserver.common.RestResponse;



public class AuthException extends RestException{
    private AuthException(RestResponse restResponse) {
        super(restResponse);
    }


    public static AuthException authorizationCodeExpired() {
        return new AuthException(
                RestResponse.forbidden(Error.INVALID_REQUEST,"Authorization code expired!")
        );
    }

    public static AuthException invalidRefreshToken() {
        return new AuthException(
                RestResponse.badRequest(Error.INVALID_REQUEST, "Refresh token is invalid!")
        );
    }

    public static AuthException invalidScope() {
        return new AuthException(
                RestResponse.badRequest(Error.INVALID_SCOPE, "The requested scope is invalid, unknown, or malformed!")
        );
    }

    public static AuthException invalidCodeChallengeMethod() {
        return new AuthException(
                RestResponse.badRequest(Error.INVALID_CODE_CHALLENGE_METHOD, "The requested code challenge method is invalid!")
        );
    }

    public static AuthException codeChallengeMissing() {
        return new AuthException(
                RestResponse.badRequest(Error.CODE_CHALLENGE_MISSING, "Code challenge was not included in the request!")
        );
    }

    public static AuthException invalidGrant() {
        return new AuthException(
                RestResponse.badRequest(Error.INVALID_GRANT, "Code challenge was not correct!")
        );
    }
}
