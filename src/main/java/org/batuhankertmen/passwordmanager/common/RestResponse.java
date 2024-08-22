package org.batuhankertmen.passwordmanager.common;

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

    @Builder.Default
    private final ErrorType error = null;

    private final String message;

    public static RestResponse ok(String message) {
        return RestResponse.builder()
                .status(HttpStatus.OK)
                .message(message)
                .build();
    }

    public static RestResponse created(String message) {
        return RestResponse.builder()
                .status(HttpStatus.CREATED)
                .message(message)
                .build();
    }

    public static RestResponse badRequest(ErrorType error, String message) {
        return RestResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .error(error)
                .message(message)
                .build();
    }

    public static RestResponse unauthorized(ErrorType error, String message) {
        return RestResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .error(error)
                .message(message)
                .build();
    }

    public static RestResponse serverError(String message) {
        return RestResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .error(ErrorType.INTERNAL_SERVER_ERROR)
                .message(message)
                .build();
    }

    public ResponseEntity<RestResponse> toResponseEntity() {
        return ResponseEntity.status(this.status).body(this);
    }
}

