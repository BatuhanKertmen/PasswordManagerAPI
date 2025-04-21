package org.batuhankertmen.oauthserver.company;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final ICompanyService companyService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CompanyRegisterDto dto) throws URISyntaxException {
        companyService.save(dto);
        return ResponseEntity
                .ok()
                .body("Company is saved successfully.");
    }
}
