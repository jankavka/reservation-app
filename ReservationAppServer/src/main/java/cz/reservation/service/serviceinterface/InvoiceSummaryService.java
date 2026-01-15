package cz.reservation.service.serviceinterface;

import cz.reservation.dto.InvoiceSummaryDto;

import java.util.List;

public interface InvoiceSummaryService {

    InvoiceSummaryDto createSummary(InvoiceSummaryDto invoiceSummaryDto);

    InvoiceSummaryDto getSummary(Long id);

    void editSummary(InvoiceSummaryDto invoiceSummaryDto, Long id);

    List<InvoiceSummaryDto> getAllSummaries();

    List<InvoiceSummaryDto> getAllSummariesByUser(Long userId);

    void deleteSummary(Long id);
}
