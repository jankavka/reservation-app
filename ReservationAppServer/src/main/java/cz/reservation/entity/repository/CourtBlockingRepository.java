package cz.reservation.entity.repository;

import cz.reservation.entity.CourtBlockingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtBlockingRepository extends JpaRepository<CourtBlockingEntity, Long> {
}
