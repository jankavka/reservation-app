package cz.reservation.entity.repository;

import cz.reservation.entity.TrainingSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TrainingSlotRepository extends
        JpaRepository<TrainingSlotEntity, Long>, JpaSpecificationExecutor<TrainingSlotEntity> {

    List<TrainingSlotEntity> findByGroupId(Long groupId);
}
