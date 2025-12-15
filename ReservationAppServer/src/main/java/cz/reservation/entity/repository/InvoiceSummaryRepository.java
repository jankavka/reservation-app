package cz.reservation.entity.repository;

import cz.reservation.entity.InvoiceSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceSummaryRepository extends JpaRepository<InvoiceSummaryEntity, Long> {

    List<InvoiceSummaryEntity> findByUserId(Long id);

    @Query(value = "SELECT * FROM invoice_summaries WHERE month = :value", nativeQuery = true)
    InvoiceSummaryEntity summaryOfCurrentMonth(@Param("value") Integer monthIntValue);

    @Query(value = "SELECT id FROM invoice_summaries WHERE month = :value", nativeQuery = true)
    Long getIdOfCurrentMonthSummary(@Param("value") Integer monthIntValue);


}
