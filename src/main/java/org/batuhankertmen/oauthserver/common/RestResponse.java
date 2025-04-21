package org.batuhankertmen.oauthserver.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse {

    @Builder.Default
    private final Instant timestamp = Instant.now();

    private final HttpStatus status;

    private final String error;

    private final String error_description;

    public static RestResponse ok(Error error, String message) {
        return RestResponse.builder()
                .status(HttpStatus.OK)
                .error(error.name())
                .error_description(message)
                .build();
    }

    public static RestResponse created(Error error, String message) {
        return RestResponse.builder()
                .status(HttpStatus.CREATED)
                .error(error.name())
                .error_description(message)
                .build();
    }

    public static RestResponse badRequest(Error error, String message) {
        return RestResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .error(error.name())
                .error_description(message)
                .build();
    }

    public static RestResponse unauthorized(Error error, String message) {
        return RestResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .error(error.name())
                .error_description(message)
                .build();
    }

    public static RestResponse forbidden(Error error, String message) {
        return RestResponse.builder()
                .status(HttpStatus.FORBIDDEN)
                .error(error.name())
                .error_description(message)
                .build();
    }

    public static RestResponse notFound(Error error, String message) {
        return RestResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .error(error.name())
                .error_description(message)
                .build();
    }

    public static RestResponse serverError(String message) {
        return RestResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .error(Error.INTERNAL_SERVER_ERROR.name())
                .error_description(message)
                .build();
    }

    public ResponseEntity<RestResponse> toResponseEntity() {
        return ResponseEntity.status(this.status).body(this);
    }
}

