package org.batuhankertmen.oauthserver.company;


import jakarta.persistence.*;
import lombok.*;
import org.batuhankertmen.oauthserver.auth.Authority;
import org.batuhankertmen.oauthserver.client.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique=true, nullable=false)
    private String username;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private boolean enabled;

    private Date createdAt;

    private Date lastUpdatedAt;


    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Authority> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Client> clientSet = new HashSet<>();

    public void addClient(Client client) {
        clientSet.add(client);
        client.setCompany(this);
    }

    public void removeClient(Client client) {
        clientSet.remove(client);
        client.setCompany(null);
    }

    public void addAuthority(Authority authority) {
        roles.add(authority);
        authority.setCompany(this);
    }

    public void removeAuthority(Authority authority) {
        roles.remove(authority);
        authority.setCompany(null);
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
