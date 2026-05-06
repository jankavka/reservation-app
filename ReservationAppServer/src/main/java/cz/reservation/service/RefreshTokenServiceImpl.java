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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;


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
                .orElseThrow(EntityNotFoundException::new)
                .getId();

        return refreshTokenRepository
                .findFirstByUserIdAndRevokedFalseOrderByIdDesc(userId)
                .orElseThrow(EntityNotFoundException::new);
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

}
