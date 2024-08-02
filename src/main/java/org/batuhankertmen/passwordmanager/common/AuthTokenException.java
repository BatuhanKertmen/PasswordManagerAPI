package org.batuhankertmen.passwordmanager.common;


public class AuthTokenException extends RuntimeException{
    private final RestResponse restResponse;

    private AuthTokenException(RestResponse restResponse) {
        this.restResponse = restResponse;
    }

    @Override
    public String getMessage() {
        return restResponse.getMessage();
    }

    public ErrorType getErrorType() {
        return restResponse.getError();
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
