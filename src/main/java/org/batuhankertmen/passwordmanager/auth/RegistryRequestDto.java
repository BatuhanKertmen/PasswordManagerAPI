package org.batuhankertmen.passwordmanager.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryRequestDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String contact;
}
