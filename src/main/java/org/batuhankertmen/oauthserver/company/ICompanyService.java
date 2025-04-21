package org.batuhankertmen.oauthserver.company;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface ICompanyService extends UserDetailsService {

    void save(CompanyRegisterDto dto);
}
