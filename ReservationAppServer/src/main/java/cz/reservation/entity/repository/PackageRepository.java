package cz.reservation.entity.repository;

import cz.reservation.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageRepository extends JpaRepository<PackageEntity, Long> {

    Optional<PackageEntity> findByPlayerId(Long playerId);
}
