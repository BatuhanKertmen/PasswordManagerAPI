package org.batuhankertmen.passwordmanager.common.exception;

import org.batuhankertmen.passwordmanager.common.ErrorType;
import org.batuhankertmen.passwordmanager.common.RestResponse;

public class FieldValidationException extends RestException {
    private FieldValidationException(RestResponse restResponse) {
        super(restResponse);
    }

    public static FieldValidationException invalidCredentialsException(ErrorType error, String message) {
        return new FieldValidationException(RestResponse.badRequest(
                error,
                message
        ));
    }
}
