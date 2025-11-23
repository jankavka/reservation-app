package cz.reservation.entity.repository;

import cz.reservation.entity.TrainingSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingSlotRepository extends JpaRepository<TrainingSlotEntity, Long> {
}
