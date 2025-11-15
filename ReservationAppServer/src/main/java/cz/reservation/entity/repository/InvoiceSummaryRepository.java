package cz.reservation.entity.repository;

import cz.reservation.entity.InvoiceSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceSummaryRepository extends JpaRepository<InvoiceSummaryEntity, Long> {
}
