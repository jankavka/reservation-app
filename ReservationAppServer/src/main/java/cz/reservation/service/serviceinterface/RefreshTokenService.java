package cz.reservation.service.serviceinterface;

import cz.reservation.dto.LoginResponseDto;
import cz.reservation.entity.RefreshToken;

public interface RefreshTokenService {

    void createRefreshToken(String username);

    RefreshToken getRefreshTokenByUsername(String username);

    LoginResponseDto setNewTokenPair(RefreshToken refreshToken, String username);

    void markedAsRevoked(RefreshToken token);

    boolean isRefreshTokenNoExpired(String token);
}
