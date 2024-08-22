package org.batuhankertmen.passwordmanager.common;

import org.batuhankertmen.passwordmanager.common.exception.RestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<RestResponse> handleException(RestException exception) {
        return exception.getRestResponse().toResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleException(Exception exception) {
        return RestResponse.serverError("Uneidentified error occured!").toResponseEntity();
    }
}
