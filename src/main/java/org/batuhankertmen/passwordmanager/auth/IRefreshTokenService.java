package org.batuhankertmen.passwordmanager.auth;

import org.batuhankertmen.passwordmanager.user.User;


public interface IRefreshTokenService {

    void save(String token, User user);

    void save(RefreshToken token);

    RefreshToken getRefreshTokenByToken(String token);

    void InvalidateRefreshTokensForUser(int userId);
}
