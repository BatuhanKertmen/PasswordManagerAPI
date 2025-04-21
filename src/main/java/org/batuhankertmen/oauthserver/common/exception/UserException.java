package org.batuhankertmen.oauthserver.common.exception;


import org.batuhankertmen.oauthserver.common.Error;
import org.batuhankertmen.oauthserver.common.RestResponse;

public class UserException extends RestException {


    protected UserException(RestResponse restResponse) {
        super(restResponse);
    }

    public static UserException sameUsernameAlreadyExists() {
        return sameUsernameAlreadyExists(
                "A user with the same username is already registered."
        );
    }

    public static UserException sameUsernameAlreadyExists(String message) {
        return new UserException(RestResponse.badRequest(Error.USERNAME_ALREADY_EXISTS, message));
    }

    public static UserException sameContactAlreadyExists() {
        return sameContactAlreadyExists(
                "A user with the same contact information is already registered."
        );
    }

    public static UserException sameContactAlreadyExists(String message) {
        return new UserException(RestResponse.badRequest(Error.CONTACT_ALREADY_EXISTS,
                message
        ));
    }

    public static UserException userNotFound() {
        return new UserException(RestResponse.notFound(Error.NOT_FOUND,
                "User not found!"
        ));
    }
}
