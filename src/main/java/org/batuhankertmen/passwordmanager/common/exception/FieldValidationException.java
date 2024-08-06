package org.batuhankertmen.passwordmanager.common.exception;

import org.batuhankertmen.passwordmanager.common.ErrorType;
import org.batuhankertmen.passwordmanager.common.RestResponse;

public class FieldValidationException extends RuntimeException {

    private final RestResponse restResponse;

    private FieldValidationException(RestResponse restResponse) {
        this.restResponse = restResponse;
    }

    @Override
    public String getMessage() {
        return restResponse.getMessage();
    }

    public ErrorType getErrorType() {
        return restResponse.getError();
    }

    public static FieldValidationException invalidCredentialsException(ErrorType error, String message) {
        return new FieldValidationException(RestResponse.badRequest(
                error,
                message
        ));
    }
}
