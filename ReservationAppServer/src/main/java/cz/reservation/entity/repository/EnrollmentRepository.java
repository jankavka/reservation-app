package cz.reservation.entity.repository;

import cz.reservation.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
}
