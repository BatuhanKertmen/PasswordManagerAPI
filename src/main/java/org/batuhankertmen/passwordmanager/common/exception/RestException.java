package org.batuhankertmen.passwordmanager.common.exception;

import lombok.Getter;
import org.batuhankertmen.passwordmanager.common.ErrorType;
import org.batuhankertmen.passwordmanager.common.RestResponse;

@Getter
public class RestException extends RuntimeException {

    private final RestResponse restResponse;

    protected RestException(RestResponse restResponse) {
        this.restResponse = restResponse;
    }

    @Override
    public String getMessage() {
        return restResponse.getMessage();
    }

    public ErrorType getErrorType() {
        return restResponse.getError();
    }

}
