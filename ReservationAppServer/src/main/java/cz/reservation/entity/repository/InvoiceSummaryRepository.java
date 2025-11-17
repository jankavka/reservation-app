package cz.reservation.entity.repository;

import cz.reservation.entity.InvoiceSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceSummaryRepository extends JpaRepository<InvoiceSummaryEntity, Long> {

    List<InvoiceSummaryEntity> findByUserId(Long id);
}
