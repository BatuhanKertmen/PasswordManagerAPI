package org.batuhankertmen.oauthserver.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.oauthserver.client.Client;
import org.batuhankertmen.oauthserver.common.exception.AuthException;
import org.batuhankertmen.oauthserver.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken findByCode(String code) {
        RefreshToken refreshToken = refreshTokenRepository.findByCode(code).orElseThrow(AuthException::invalidRefreshToken);

        if (!refreshToken.isActive()) {
            deactivateAllTokensForUserAndClient(refreshToken.getUser(), refreshToken.getClient());
            throw AuthException.invalidRefreshToken();
        }

        return refreshToken;
    }

    @Override
    public RefreshToken createRefreshToken() {

        return RefreshToken.builder()
                .active(true)
                .code(JwtService.createHexadecimalEncodedRandomBytes(32))
                .build();
    }

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void disable(RefreshToken refreshToken) {
        refreshToken.setActive(false);
        refreshTokenRepository.save(refreshToken);
    }

    private void deactivateAllTokensForUserAndClient(User user, Client client) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUserAndClientAndActiveTrue(user, client);

        activeTokens.forEach(token -> token.setActive(false));

        refreshTokenRepository.saveAll(activeTokens);
    }
}
