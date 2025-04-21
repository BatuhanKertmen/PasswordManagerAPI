package org.batuhankertmen.oauthserver.auth;

import lombok.Getter;

@Getter
public enum CodeChallengeMethod {
    PLAIN("plain"),
    S256("S256");

    private final String codeChallengeMethod;

    CodeChallengeMethod(String codeChallengeMethod) {
        this.codeChallengeMethod = codeChallengeMethod;
    }

    public static CodeChallengeMethod fromString(String challengeMethod) {
        for (CodeChallengeMethod method : CodeChallengeMethod.values()) {
            if (method.name().equalsIgnoreCase(challengeMethod)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid scope name: " + challengeMethod);
    }
}
