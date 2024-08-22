package org.batuhankertmen.passwordmanager.common.exception;


import org.batuhankertmen.passwordmanager.common.ErrorType;
import org.batuhankertmen.passwordmanager.common.RestResponse;

public class UserException extends RestException {


    protected UserException(RestResponse restResponse) {
        super(restResponse);
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

    public static UserException invalidPasswordException(ErrorType error, String message) {
        return new UserException(RestResponse.badRequest(
                error,
                message
        ));
    }
}
