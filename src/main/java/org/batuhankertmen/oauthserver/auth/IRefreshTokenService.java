package org.batuhankertmen.oauthserver.auth;

public interface IRefreshTokenService {
    RefreshToken findByCode(String code);

    RefreshToken createRefreshToken();

    void save(RefreshToken refreshToken);

    void disable(RefreshToken refreshToken);
}
