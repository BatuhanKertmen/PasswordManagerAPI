package org.batuhankertmen.passwordmanager.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryRequestDto {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 5, max = 64, message = "Username must be between 5 and 64 characters")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 5, max = 64, message = "Username must be between 5 and 64 characters")
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 5, max = 64, message = "Username must be between 5 and 64 characters")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 5, max = 64, message = "Username must be between 5 and 64 characters")
    private String contact;

    @JsonProperty("remember_me")
    private boolean rememberMe;
}
