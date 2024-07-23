package org.batuhankertmen.passwordmanager.common;


public class UserException extends RuntimeException {

    private final RestResponse restResponse;

    private UserException(RestResponse restResponse) {
        this.restResponse = restResponse;
    }

    @Override
    public String getMessage() {
        return restResponse.getMessage();
    }

    public ErrorType getErrorType() {
        return restResponse.getError();
    }

    public static UserException sameUsernameAlreadyExists() {
        return sameUsernameAlreadyExists(
                ErrorType.USER_WITH_USERNAME_ALREADY_EXISTS,
                "A user with the same username is already registered."
        );
    }

    public static UserException sameUsernameAlreadyExists(ErrorType error, String message) {
        return new UserException(RestResponse.badRequest(
                error,
                message));
    }

    public static UserException sameContactAlreadyExists() {
        return sameContactAlreadyExists(
                ErrorType.USER_WITH_CONTACT_ALREADY_EXISTS,
                "A user with the same contact information is already registered."
        );
    }

    public static UserException sameContactAlreadyExists(ErrorType error, String message) {
        return new UserException(RestResponse.badRequest(
                error,
                message
        ));
    }
}
