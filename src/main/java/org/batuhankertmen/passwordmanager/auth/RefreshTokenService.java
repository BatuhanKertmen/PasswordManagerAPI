package org.batuhankertmen.passwordmanager.auth;


import lombok.RequiredArgsConstructor;
import org.batuhankertmen.passwordmanager.common.exception.AuthTokenException;
import org.batuhankertmen.passwordmanager.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void save(String token, User user) {
        this.save(
                RefreshToken.builder()
                        .token(token)
                        .isValid(true)
                        .user(user)
                        .build()
        );
    }

    @Override
    public void save(RefreshToken token) {
        refreshTokenRepository.save(token);
    }

    @Override
    public RefreshToken getRefreshTokenByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(
                AuthTokenException::refreshTokenInvalid
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void InvalidateRefreshTokensForUser(int userId) {
        refreshTokenRepository.invalidateAllRefreshTokensByUser(userId);
    }
}
