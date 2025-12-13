package cz.reservation.entity.repository;

import cz.reservation.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query("select b from BookingEntity b where b.trainingSlot.id = :slotId ")
    List<BookingEntity> findAllByTrainingSlotId(@Param("slotId") Long slotId);
}