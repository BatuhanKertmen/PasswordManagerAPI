package org.batuhankertmen.passwordmanager.common.exception;


import org.batuhankertmen.passwordmanager.common.ErrorType;
import org.batuhankertmen.passwordmanager.common.RestResponse;

public class AuthTokenException extends RestException{
    private AuthTokenException(RestResponse restResponse) {
        super(restResponse);
    }

    public static AuthTokenException refreshTokenNotFound() {
        return new AuthTokenException(
                RestResponse.unauthorized(ErrorType.UNAUTHORIZED, "Refresh token not found or invalid")
        );
    }

    public static AuthTokenException refreshTokenInvalid() {
        return new AuthTokenException(
                RestResponse.unauthorized(ErrorType.UNAUTHORIZED, "Refresh token not found or invalid")
        );
    }
}
