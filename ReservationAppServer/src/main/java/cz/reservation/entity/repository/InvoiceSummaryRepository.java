package cz.reservation.entity.repository;

import cz.reservation.entity.InvoiceSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceSummaryRepository extends
        JpaRepository<InvoiceSummaryEntity, Long>, JpaSpecificationExecutor<InvoiceSummaryEntity> {

    List<InvoiceSummaryEntity> findByPlayerId(Long id);

    @Query(value = "SELECT * FROM invoice_summaries WHERE month = :value", nativeQuery = true)
    Optional<InvoiceSummaryEntity> getSummaryOfCurrentMonth(@Param("value") Integer monthIntValue);
}
