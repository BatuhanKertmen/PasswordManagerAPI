package org.batuhankertmen.oauthserver.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenRequestDto {
    @JsonProperty(required = true)
    private String code;

    @JsonProperty(value = "grant_type", required = true)
    private String grantType;

    @JsonProperty(value = "redirect_uri", required = true)
    private String redirectUri;

    @JsonProperty(value = "code_verifier", required = true)
    private String codeVerifier;

    @JsonProperty(value = "client_id", required = true)
    private String clientId;

    @JsonProperty(value = "client_secret", required = true)
    private String clientSecret;
}
