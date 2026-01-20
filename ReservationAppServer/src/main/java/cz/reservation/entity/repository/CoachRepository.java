package cz.reservation.entity.repository;

import cz.reservation.entity.CoachEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<CoachEntity, Long> {
}
