package cz.reservation.entity.repository;

import cz.reservation.entity.CoachEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CoachRepository extends JpaRepository<CoachEntity, Long> {
}
