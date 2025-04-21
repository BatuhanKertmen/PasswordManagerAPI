package org.batuhankertmen.oauthserver.user;

import jakarta.persistence.*;
import lombok.*;
import org.batuhankertmen.oauthserver.auth.Authority;
import org.batuhankertmen.oauthserver.auth.AuthorizationCode;
import org.batuhankertmen.oauthserver.auth.RefreshToken;
import org.batuhankertmen.oauthserver.auth.Scope;
import org.batuhankertmen.oauthserver.client.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

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

    @Column(length = 64)
    private String firstName;

    @Column(length = 64)
    private String lastName;

    @Column(length = 64)
    private String middleName;

    @Column(length = 256)
    private String address;

    @Column(length = 256)
    private String profilePicture;

    @Column(length = 128)
    private String email;

    private boolean emailVerified;

    private Date birthday;

    @Column(length = 16)
    private String phoneNumber;

    private boolean phoneNumberVerified;

    @Column(length = 4)
    private String locale;

    private Date createdAt;

    private Date lastUpdatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Authority> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AuthorizationCode> authorizationCodes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Scope> scopes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    public void addAuthority(Authority authority) {
        roles.add(authority);
        authority.setUser(this);
    }

    public void removeAuthority(Authority authority) {
        roles.remove(authority);
        authority.setUser(null);
    }

    public void addRefreshToken(RefreshToken token) {
        refreshTokens.add(token);
        token.setUser(this);
    }

    public void removeRefreshToken(RefreshToken token) {
        refreshTokens.remove(token);
        token.setUser(null);
    }

    public void addScope(Scope scope) {
        scopes.add(scope);
        scope.setUser(this);
    }

    public void removeScope(Scope scope) {
        scopes.remove(scope);
        scope.setUser(null);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
