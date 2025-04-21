package org.batuhankertmen.oauthserver.company;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/company")
public class CompanyMvcController {
    @GetMapping("register")
    public String register() {
        return "company/register.html";
    }

    @GetMapping("login")
    public String login(@RequestParam(required = false) String error) {
        return "company/login.html";
    }
}
