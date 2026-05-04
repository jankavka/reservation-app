package cz.reservation.entity.repository;

import cz.reservation.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String accessToken);

    Optional<RefreshToken> findFirstByUserIdAndRevokedFalse(Long userId);
}
