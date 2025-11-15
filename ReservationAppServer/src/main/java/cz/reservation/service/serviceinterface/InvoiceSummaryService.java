package cz.reservation.service.serviceinterface;

import cz.reservation.dto.InvoiceSummaryDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InvoiceSummaryService {

    ResponseEntity<InvoiceSummaryDto> createSummary(InvoiceSummaryDto invoiceSummaryDto);

    ResponseEntity<InvoiceSummaryDto> getSummary(Long id);

    ResponseEntity<InvoiceSummaryDto> editSummary(InvoiceSummaryDto invoiceSummaryDto, Long id);

    ResponseEntity<List<InvoiceSummaryDto>> getAllSummaries();

    ResponseEntity<List<InvoiceSummaryDto>> getAllSummariesByUser(Long userId);
}
