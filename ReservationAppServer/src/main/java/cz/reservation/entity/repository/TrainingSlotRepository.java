package cz.reservation.entity.repository;

import cz.reservation.entity.TrainingSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingSlotRepository extends JpaRepository<TrainingSlotEntity, Long> {

    List<TrainingSlotEntity> findByGroupId(Long groupId);


}
