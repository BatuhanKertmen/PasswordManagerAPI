package org.batuhankertmen.oauthserver.auth;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.user.User;

import java.util.Objects;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id, client_client_id, scope"}))
public class Scope {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Client client;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scope)) return false;
        Scope scopeObj = (Scope) o;
        return Objects.equals(scope, scopeObj.getScope());
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope);
    }

    @Override
    public String toString() {
        return String.valueOf(scope);
    }
}

