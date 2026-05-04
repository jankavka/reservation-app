package cz.reservation.service.serviceinterface;

import cz.reservation.entity.RefreshToken;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String username);

    RefreshToken getRefreshToken(String token);

    RefreshToken getRefreshTokenByUsername(String username);

    void setNewTokenPair(RefreshToken oldToken, HttpServletResponse response, String username) throws IOException;

    void refreshTokenExpiredResponse(HttpServletResponse response, String message) throws IOException;

    void markedAsRevoked(RefreshToken token);

    boolean isRefreshTokenNoExpired(String token);
}
