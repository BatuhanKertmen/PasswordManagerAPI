package org.batuhankertmen.oauthserver.company;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyRegisterDto {
    private String username;
    private String password;
}
