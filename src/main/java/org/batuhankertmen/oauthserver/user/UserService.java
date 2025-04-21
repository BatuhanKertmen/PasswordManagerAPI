package org.batuhankertmen.oauthserver.user;

import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.auth.IScopeService;
import org.batuhankertmen.oauthserver.auth.Scope;
import org.batuhankertmen.oauthserver.auth.ScopeRepository;
import org.batuhankertmen.oauthserver.auth.ScopeService;
import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.client.ClientRepository;
import org.batuhankertmen.oauthserver.common.exception.ClientException;
import org.batuhankertmen.oauthserver.common.exception.UserException;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Primary
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final ClientRepository clientRepository;

    private final IScopeService scopeService;

    @Override
    @Transactional(readOnly = true)
    public HashSet<Scope> getAllowedScopes(String clientId) {
        User user = getAuthenticatedUser();

        return scopeService.getAllScopesByUserAndClientId(user, clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public  Map<Client, List<Scope>> getAllowedScopesPerClient() {
        User user = getAuthenticatedUser();

        return user.getScopes().stream().collect(Collectors.groupingBy(Scope::getClient));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private User getAuthenticatedUser() {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameAndEnabledTrue(authenticatedUser).orElseThrow(UserException::userNotFound);
    }

    @Override
    @Transactional
    public void revokeClientPermissions(String clientId) {
        User user = getAuthenticatedUser();
        Client client = clientRepository.findById(clientId).orElseThrow(ClientException::clientNotFoundException);

        scopeService.deleteAllScopesByUserAndClient(user, client);
    }


}
