package org.batuhankertmen.oauthserver.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClientRegisterResponseDto {
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String clientSecret;
}
