package cz.reservation.entity.repository;

import cz.reservation.entity.TrainingSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingSlotsRepository extends JpaRepository<TrainingSlotEntity, Long> {
}
