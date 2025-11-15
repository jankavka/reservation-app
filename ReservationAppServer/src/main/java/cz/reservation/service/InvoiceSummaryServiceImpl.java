package cz.reservation.service;

import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.dto.mapper.InvoiceSummaryMapper;
import cz.reservation.entity.repository.InvoiceSummaryRepository;
import cz.reservation.service.serviceinterface.InvoiceSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceSummaryServiceImpl implements InvoiceSummaryService {

    private final InvoiceSummaryRepository invoiceSummaryRepository;

    private final InvoiceSummaryMapper invoiceSummaryMapper;

    @Autowired
    public InvoiceSummaryServiceImpl(
            InvoiceSummaryMapper invoiceSummaryMapper,
            InvoiceSummaryRepository invoiceSummaryRepository){
        this.invoiceSummaryMapper = invoiceSummaryMapper;
        this.invoiceSummaryRepository = invoiceSummaryRepository;
    }


    @Override
    public ResponseEntity<InvoiceSummaryDto> createSummary(InvoiceSummaryDto invoiceSummaryDto) {
        return null;
    }

    @Override
    public ResponseEntity<InvoiceSummaryDto> getSummary(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<InvoiceSummaryDto> editSummary(InvoiceSummaryDto invoiceSummaryDto, Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<InvoiceSummaryDto>> getAllSummaries() {
        return null;
    }

    @Override
    public ResponseEntity<List<InvoiceSummaryDto>> getAllSummariesByUser(Long userId) {
        return null;
    }
}
