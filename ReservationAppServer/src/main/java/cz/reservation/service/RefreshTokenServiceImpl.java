package cz.reservation.service;

import cz.reservation.dto.LoginResponseDto;
import cz.reservation.entity.RefreshToken;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.RefreshTokenRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.JwtService;
import cz.reservation.service.serviceinterface.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private static final String SERVICE_NAME = "Refresh token";


    @Value("${security.jwt.refresh-exp}")
    private long jwtRefreshExp;


    @Transactional
    @Override
    public void createRefreshToken(String username) {
        UserEntity user = userRepository.findByEmail(username).orElseThrow(EntityNotFoundException::new);
        RefreshToken token = RefreshToken.builder()
                .token(jwtService.generateRefreshToken(username))
                .user(user)
                .revoked(false)
                .expirationDate(Instant.now().plusMillis(jwtRefreshExp))
                .build();

        refreshTokenRepository.save(token);

    }

    @Transactional(readOnly = true)
    @Override
    public RefreshToken getRefreshTokenByUsername(String username) {
        var userId = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("Not found refresh token with username" + username))
                .getId();

        return refreshTokenRepository
                .findFirstByUserIdAndRevokedFalseOrderByIdDesc(userId)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, userId)));
    }


    @Transactional
    @Override
    public LoginResponseDto setNewTokenPair(RefreshToken refreshToken, String username) {
        var newAccessToken = jwtService.generateAccessToken(username);
        var expirationTime = jwtService.extractExpiration(newAccessToken);
        createRefreshToken(username);
        return new LoginResponseDto(newAccessToken, expirationTime.toInstant().toEpochMilli());
    }

    @Transactional
    @Override
    public void markedAsRevoked(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Override
    public boolean isRefreshTokenNoExpired(String token) {
        try {
            return !jwtService.isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.warn("{}, {}", e.getMessage(), e.getClass());
            return false;
        }
    }

    @Transactional
    @Override
    public void deleteRefreshToken(Long id) {
        if (refreshTokenRepository.existsById(id)) {
            refreshTokenRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }

    @Override
    public List<RefreshToken> getAllRefreshTokens() {
        return refreshTokenRepository.findAll();

    }

    //TODO: make repo methods which will replace cycle
    @Scheduled(cron = "@monthly")
    public void deleteOldTokens() {
        var tokens = getAllRefreshTokens();
        for (RefreshToken t : tokens) {
            if (t.getExpirationDate().isBefore(Instant.now())) {
                refreshTokenRepository.delete(t);
            }
            if (t.isRevoked()) {
                refreshTokenRepository.delete(t);
            }
        }
    }

}
