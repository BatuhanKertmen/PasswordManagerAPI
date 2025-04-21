package org.batuhankertmen.oauthserver.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryRequestDto {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty(value = "middle_name")
    private String middleName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty(required = true)
    private String username;

    @JsonProperty(required = true)
    private String password;

    @JsonProperty(required = true)
    private String email;

    private String address;

    @JsonProperty("profile_picture")
    private String profilePicture;

    private String birthday;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String locale;

    @JsonProperty("remember_me")
    private boolean rememberMe;
}
