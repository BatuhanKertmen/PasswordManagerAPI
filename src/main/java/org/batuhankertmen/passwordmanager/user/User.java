package org.batuhankertmen.passwordmanager.user;

import jakarta.persistence.*;
import lombok.*;
import org.batuhankertmen.passwordmanager.auth.RefreshToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(unique=true, nullable=false)
    private String username;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private boolean enabled;

    private String firstName;

    private String lastName;

    @Column(unique=true)
    private String contact;

    private Date createdAt;

    private Date lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy="user", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<RefreshToken> refreshTokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
