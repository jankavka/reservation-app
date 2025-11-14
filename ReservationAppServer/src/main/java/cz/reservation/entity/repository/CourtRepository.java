package cz.reservation.entity.repository;

import cz.reservation.entity.CourtEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<CourtEntity, Long> {
}
