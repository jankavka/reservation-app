package cz.reservation.controller;

import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.service.serviceinterface.InvoiceSummaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoice-summary")
public class InvoiceSummaryController {

    InvoiceSummaryService invoiceSummaryService;

    @Autowired
    public InvoiceSummaryController(InvoiceSummaryService invoiceSummaryService) {
        this.invoiceSummaryService = invoiceSummaryService;
    }

    @PostMapping
    public ResponseEntity<InvoiceSummaryDto> createInvoiceSummary(
            @RequestBody @Valid InvoiceSummaryDto invoiceSummaryDto) throws IOException {
        return invoiceSummaryService.createSummary(invoiceSummaryDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceSummaryDto> getSummary(@PathVariable Long id) {
        return invoiceSummaryService.getSummary(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editSummary(
            @RequestBody @Valid InvoiceSummaryDto invoiceSummaryDto,
            @PathVariable Long id) {

        return invoiceSummaryService.editSummary(invoiceSummaryDto, id);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceSummaryDto>> getAllSummaries() {
        return invoiceSummaryService.getAllSummaries();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvoiceSummaryDto>> getAllSummariesByUser(@PathVariable Long userId) {
        return invoiceSummaryService.getAllSummariesByUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSummary(@PathVariable Long id) {
        return invoiceSummaryService.deleteSummary(id);
    }

}
