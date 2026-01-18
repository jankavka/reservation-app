package cz.reservation.entity.repository;

import cz.reservation.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttendanceRepository
        extends JpaRepository<AttendanceEntity, Long>, JpaSpecificationExecutor<AttendanceEntity> {
}
