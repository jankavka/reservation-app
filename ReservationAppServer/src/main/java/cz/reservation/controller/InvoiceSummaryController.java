package cz.reservation.controller;

import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.service.serviceinterface.InvoiceSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-summary")
@RequiredArgsConstructor
public class InvoiceSummaryController {

    private final InvoiceSummaryService invoiceSummaryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceSummaryDto createInvoiceSummary(@RequestBody @Valid InvoiceSummaryDto invoiceSummaryDto) {
        return invoiceSummaryService.createSummary(invoiceSummaryDto);
    }

    @GetMapping("/{id}")
    public InvoiceSummaryDto getSummary(@PathVariable Long id) {
        return invoiceSummaryService.getSummary(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editSummary(@RequestBody @Valid InvoiceSummaryDto invoiceSummaryDto, @PathVariable Long id) {
        invoiceSummaryService.editSummary(invoiceSummaryDto, id);
    }

    @GetMapping
    public List<InvoiceSummaryDto> getAllSummaries() {
        return invoiceSummaryService.getAllSummaries();
    }

    @GetMapping("/user/{userId}")
    public List<InvoiceSummaryDto> getAllSummariesByUser(@PathVariable Long userId) {
        return invoiceSummaryService.getAllSummariesByUser(userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSummary(@PathVariable Long id) {
        invoiceSummaryService.deleteSummary(id);
    }

}
