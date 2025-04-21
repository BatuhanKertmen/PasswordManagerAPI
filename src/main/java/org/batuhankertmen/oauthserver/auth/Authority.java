package org.batuhankertmen.oauthserver.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.batuhankertmen.oauthserver.company.Company;
import org.batuhankertmen.oauthserver.user.User;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "authority")
    private Role role;

    @ManyToOne()
    private User user;

    @ManyToOne()
    private Company company;

    @Override
    public String getAuthority() {
        return "ROLE_" + role.name();
    }

    @Override
    public String toString() { return role.name(); }
}
