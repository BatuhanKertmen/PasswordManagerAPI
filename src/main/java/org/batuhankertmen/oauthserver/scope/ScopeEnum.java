package org.batuhankertmen.oauthserver.scope;


public enum ScopeEnum {
    DEFAULT("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip"),

    // Open ID Connect scopes
    OPENID("Identifies the user and provides Single Sign-On (SSO) services."),

    // username, firstname, middlename, lastname, profile pic, birthday
    PROFILE("Grants access to basic profile information about the user. This typically includes non-sensitive personal data like name, family_name, given_name, nickname, preferred_username, profile (URL to the user’s profile), picture (profile photo URL), and updated_at."),

    // email, email verified
    EMAIL("Grants access to the user’s email address and email verification status. Includes the email and email verified claims in the ID token."),

    // address, locale
    ADDRESS("Grants access to the user’s postal address. This includes claims such as formatted (the full formatted address), street_address, locality (city), region (state), postal_code, and country."),

    // refresh token
    OFFLINE_ACCESS("Requests a refresh token to allow the application to access resources when the user is not actively using the application.");

    private final String description;

    ScopeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static ScopeEnum fromString(String scopeName) {
        for (ScopeEnum scopeEnum : ScopeEnum.values()) {
            if (scopeEnum.name().equalsIgnoreCase(scopeName)) {
                return scopeEnum;
            }
        }
        throw new IllegalArgumentException("Invalid scope name: " + scopeName);
    }
}
