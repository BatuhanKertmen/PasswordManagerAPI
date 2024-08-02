package org.batuhankertmen.passwordmanager.auth;

import jakarta.persistence.*;
import lombok.*;
import org.batuhankertmen.passwordmanager.user.User;

@Data
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String token;

    private boolean isValid;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
