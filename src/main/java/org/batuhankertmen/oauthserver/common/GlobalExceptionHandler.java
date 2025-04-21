package org.batuhankertmen.oauthserver.common;


import org.batuhankertmen.oauthserver.common.exception.RestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.security.core.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public  ResponseEntity<RestResponse> handleException(AuthenticationException exception) {
        return RestResponse.unauthorized(Error.UNAUTHORIZED_USER, exception.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(RestException.class)
    public ResponseEntity<RestResponse> handleException(RestException exception) {
        return exception.getRestResponse().toResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleException(Exception exception) {
        return RestResponse.serverError( exception.getMessage()).toResponseEntity();
    }
}
