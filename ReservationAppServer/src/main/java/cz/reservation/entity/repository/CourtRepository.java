package cz.reservation.entity.repository;

import cz.reservation.entity.CourtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourtRepository extends JpaRepository<CourtEntity, Long>, JpaSpecificationExecutor<CourtEntity> {
}
