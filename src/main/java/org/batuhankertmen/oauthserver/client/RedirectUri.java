package org.batuhankertmen.oauthserver.client;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.auth.AuthorizationCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class RedirectUri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String uri;

    @Builder.Default
    @OneToMany(mappedBy = "redirectUri", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<AuthorizationCode> authorizationCodeSet = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    public void addAuthorizationCode(AuthorizationCode code) {
        authorizationCodeSet.add(code);
        code.setRedirectUri(this);
    }

    public void removeAuthorizationCode(AuthorizationCode code) {
        authorizationCodeSet.remove(code);
        code.setRedirectUri(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RedirectUri)) return false;
        RedirectUri redirectUri = (RedirectUri) o;
        return Objects.equals(uri, redirectUri.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public String toString() {
        return uri;
    }
}
