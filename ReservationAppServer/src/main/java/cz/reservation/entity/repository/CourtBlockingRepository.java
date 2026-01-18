package cz.reservation.entity.repository;

import cz.reservation.entity.CourtBlockingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourtBlockingRepository extends
        JpaRepository<CourtBlockingEntity, Long>, JpaSpecificationExecutor<CourtBlockingEntity> {
}
