package cz.reservation.entity.repository;

import cz.reservation.entity.SeasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonRepository extends JpaRepository<SeasonEntity, Long> {
}
