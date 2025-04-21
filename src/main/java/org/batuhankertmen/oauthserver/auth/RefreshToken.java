package org.batuhankertmen.oauthserver.auth;

import jakarta.persistence.*;
import lombok.*;
import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.user.User;

import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshToken)) return false;
        RefreshToken refreshToken = (RefreshToken) o;
        return Objects.equals(code, refreshToken.code);
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
