package org.batuhankertmen.oauthserver.auth;

import jakarta.persistence.*;
import lombok.*;
import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.client.RedirectUri;
import org.batuhankertmen.oauthserver.user.User;

import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationCode {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String code;

    @Column(name="created_at")
    private Date createdAt;

    private boolean active;

    @Column(length = 128)
    private String codeChallenge;

    @Enumerated(EnumType.STRING)
    @Column(length = 5)
    private CodeChallengeMethod codeChallengeMethod;

    private String nonce;

    @ManyToOne
    private RedirectUri redirectUri;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void addUserAndClient(User currentUser, Client currentClient) {
        user = currentUser;
        client = currentClient;

        currentUser.getAuthorizationCodes().add(this);
        currentClient.getAuthorizationCodeList().add(this);
    }

    public void removeUserAndClient(User currentUser, Client currentClient) {
        user = null;
        client = null;

        currentUser.getAuthorizationCodes().remove(this);
        currentClient.getAuthorizationCodeList().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorizationCode)) return false;
        AuthorizationCode authorizationCode = (AuthorizationCode) o;
        return Objects.equals(code, authorizationCode.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
