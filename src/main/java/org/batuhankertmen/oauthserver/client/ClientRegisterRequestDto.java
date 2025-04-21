package org.batuhankertmen.oauthserver.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegisterRequestDto {
    private String name;

    @JsonProperty("program_type")
    private String programType;

    @JsonProperty("redirect_uri_list")
    private List<String> redirectUriList;
}
