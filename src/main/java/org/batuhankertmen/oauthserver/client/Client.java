package org.batuhankertmen.oauthserver.client;

import jakarta.persistence.*;
import lombok.*;
import org.batuhankertmen.oauthserver.auth.AuthorizationCode;
import org.batuhankertmen.oauthserver.auth.RefreshToken;
import org.batuhankertmen.oauthserver.auth.Scope;
import org.batuhankertmen.oauthserver.company.Company;
import org.batuhankertmen.oauthserver.user.User;

import java.util.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Client {

    @Id
    @Column(name = "client_id")
    private String clientId;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type")
    private ClientType clientType;

    @Column(name = "client_secret")
    private String clientSecret;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RedirectUri> redirectUriList = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AuthorizationCode> authorizationCodeList = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Scope> scopes = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    public void addRedirectUri(RedirectUri uri) {
        redirectUriList.add(uri);
        uri.setClient(this);
    }

    public void removeRedirectUri(RedirectUri comment) {
        redirectUriList.remove(comment);
        comment.setClient(null);
    }

    public void addRefreshToken(RefreshToken token) {
        refreshTokens.add(token);
        token.setClient(this);
    }

    public void removeRefreshToken(RefreshToken token) {
        refreshTokens.remove(token);
        token.setClient(null);
    }

    public void addScope(Scope scope) {
        scopes.add(scope);
        scope.setClient(this);
    }

    public void removeScope(Scope scope) {
        scopes.remove(scope);
        scope.setClient(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return Objects.equals(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }

    @Override
    public String toString() {
        return clientId;
    }
}
