package cz.reservation.service.serviceinterface;

import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.entity.filter.InvoiceSummaryFilter;

import java.util.List;

public interface InvoiceSummaryService {

    InvoiceSummaryDto createSummary(InvoiceSummaryDto invoiceSummaryDto);

    InvoiceSummaryDto getSummary(Long id);

    void editSummary(InvoiceSummaryDto invoiceSummaryDto, Long id);

    List<InvoiceSummaryDto> getAllSummaries(InvoiceSummaryFilter invoiceSummaryFilter);

    List<InvoiceSummaryDto> getAllSummariesByUser(Long userId);

    void deleteSummary(Long id);
}
