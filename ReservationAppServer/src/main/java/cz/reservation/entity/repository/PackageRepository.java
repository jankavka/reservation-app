package cz.reservation.entity.repository;

import cz.reservation.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PackageRepository extends JpaRepository<PackageEntity, Long>, JpaSpecificationExecutor<PackageEntity> {

    Optional<PackageEntity> findByPlayerId(Long playerId);
}
