package org.batuhankertmen.oauthserver.common.exception;

import lombok.Getter;
import org.batuhankertmen.oauthserver.common.Error;
import org.batuhankertmen.oauthserver.common.RestResponse;

@Getter
public class RestException extends RuntimeException {

    private final RestResponse restResponse;

    protected RestException(RestResponse restResponse) {
        this.restResponse = restResponse;
    }

    @Override
    public String getMessage() {
        return restResponse.getError_description();
    }


    public static RestException internalServerError(String message) {
        return new RestException(RestResponse.serverError(message));
    }

    public static RestException invalidSessionException() {
        return new RestException(RestResponse.forbidden(Error.INVALID_SESSION,
                "Current session does not have necessary information"
        ));
    }

    public static RestException badRequest(Error error, String message) {
        return new RestException(RestResponse.badRequest(error, message));
    }
}
