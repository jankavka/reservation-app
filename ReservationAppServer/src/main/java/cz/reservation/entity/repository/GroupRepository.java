package cz.reservation.entity.repository;

import cz.reservation.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupEntity, Long>, JpaSpecificationExecutor<GroupEntity> {

    List<GroupEntity> findByCoachId(Long coachId);
}
