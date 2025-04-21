package org.batuhankertmen.oauthserver.user;

import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.auth.Scope;
import org.batuhankertmen.oauthserver.client.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserMvcController {

    private final IUserService userService;

    @GetMapping("/permissions")
    public String permissions(Model model) {
        Map<Client, List<Scope>> scopes = userService.getAllowedScopesPerClient();

        model.addAttribute("authorizedClients", scopes);
        return "user/authorized-clients.html";
    }

    @GetMapping("/revoke")
    public String revoke(@RequestParam String clientId) {
        userService.revokeClientPermissions(clientId);

        return "redirect:/user/permissions";
    }
}
