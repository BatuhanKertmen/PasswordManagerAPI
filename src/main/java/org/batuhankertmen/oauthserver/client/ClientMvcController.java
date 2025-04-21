package org.batuhankertmen.oauthserver.client;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.auth.IAuthorizationService;
import org.batuhankertmen.oauthserver.auth.IScopeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientMvcController {

    private final IClientService clientService;

    private final IAuthorizationService authorizationService;

    private final IScopeService scopeService;

    @GetMapping("/authorize")
    public RedirectView authorize(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("response_type") String responseType,
            @RequestParam(required = false) String scope,
            @RequestParam String state,
            @RequestParam("code_challenge") String codeChallenge,
            @RequestParam("code_challenge_method") String codeChallengeMethod,
            @RequestParam(value = "nonce", required = false) String nonce,
            HttpSession session
    ) {
        return clientService.authorizeClient(session, clientId, redirectUri, responseType, scope, state, codeChallenge, codeChallengeMethod, nonce);
    }

    @GetMapping("/allow")
    public RedirectView allow(HttpSession session) {
        String redirectPath = authorizationService.processAuthorization(session);

        return new RedirectView(redirectPath);
    }

    @GetMapping("/register")
    public String register() {
        return "client/client_register.html";
    }

    @GetMapping("/permission")
    public String permission(
            @RequestParam String redirect_uri,
            @RequestParam("scopes") String notAllowedScopes,
            Model model)
    {
        List<String> scopeList = Arrays.asList(notAllowedScopes.split(","));
        Map<String, String> scopeAndDescription = scopeService.getScopeAndDescriptionPairs(scopeList);

        model.addAttribute("redirect_uri", redirect_uri);
        model.addAttribute("scopes", scopeAndDescription);
        return "client/permission.html";
    }

}
