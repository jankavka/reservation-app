package cz.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.reservation.entity.RefreshToken;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.RefreshTokenRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.JwtService;
import cz.reservation.service.serviceinterface.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Value("${security.jwt.refresh-exp}")
    private long jwtRefreshExp;


    @Transactional
    @Override
    public RefreshToken createRefreshToken(String username) {
        UserEntity user = userRepository.findByEmail(username).orElseThrow(EntityNotFoundException::new);
        RefreshToken token = RefreshToken.builder()
                .token(jwtService.generateRefreshToken(username))
                .user(user)
                .revoked(false)
                .expirationDate(Instant.now().plusMillis(jwtRefreshExp))
                .build();

        return refreshTokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    @Override
    public RefreshToken getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public RefreshToken getRefreshTokenByUsername(String username) {
        var userId = userRepository.findByEmail(username)
                .orElseThrow(EntityNotFoundException::new)
                .getId();

        return refreshTokenRepository
                .findFirstByUserIdAndRevokedFalse(userId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    @Override
    public void setNewTokenPair(
            RefreshToken refreshToken,
            HttpServletResponse response,
            String username) throws IOException {

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);
        wrappedResponse.resetBuffer();
        var newAccessToken = jwtService.generateAccessToken(username);
        createRefreshToken(username);
        wrappedResponse
                .getWriter()
                .write(objectMapper.writeValueAsString(Map.of("accessToken", newAccessToken)));
        wrappedResponse.setStatus(401);
        wrappedResponse.copyBodyToResponse();
    }

    @Transactional
    @Override
    public void refreshTokenExpiredResponse(HttpServletResponse response, String message) throws IOException {
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        wrappedResponse.resetBuffer();
        wrappedResponse.setStatus(401);
        wrappedResponse
                .getWriter()
                .write(objectMapper.writeValueAsString(Map.of("message", message)));
        wrappedResponse.copyBodyToResponse();
    }

    @Transactional
    @Override
    public void markedAsRevoked(RefreshToken token) {
        token.setRevoked(true);
    }

    @Override
    public boolean isRefreshTokenNoExpired(String token) {
        try {
            return !jwtService.isTokenExpired(token);
        } catch(ExpiredJwtException e){
            log.warn("{}, {}", e.getMessage(), e.getClass());
            return false;
        }
    }

}
