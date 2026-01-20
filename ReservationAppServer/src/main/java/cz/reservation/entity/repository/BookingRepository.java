package cz.reservation.entity.repository;

import cz.reservation.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long>, JpaSpecificationExecutor<BookingEntity> {

    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.player.id = :playerId " +
            "AND b.trainingSlot.startAt >= :startDate " +
            "AND b.trainingSlot.startAt < :endDate")
    List<BookingEntity> findByPlayerIdAndTrainingSlotStartAtBetween(
            @Param("playerId") Long playerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Modifying
    @Query("DELETE FROM BookingEntity b WHERE b.bookedAt < :cutoffDate")
    void deleteByBookedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}