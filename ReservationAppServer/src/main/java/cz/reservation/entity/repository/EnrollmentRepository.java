package cz.reservation.entity.repository;

import cz.reservation.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {

    @Query(value = "select count(e) from EnrollmentEntity e where e.group.id = :groupId and e.state = ACTIVE")
    Integer countEnrollmentsByGroupId(@Param("groupId") Long groupId);

    List<EnrollmentEntity> findByGroupId(Long groupId);


}
