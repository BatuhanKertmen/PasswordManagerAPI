package org.batuhankertmen.oauthserver.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error) {
        return "auth/login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register.html";
    }

}
