package cz.reservation.service.invoice;

import cz.reservation.entity.InvoiceSummaryEntity;

import java.io.FileNotFoundException;

public interface InvoiceEngine {

    void createInvoice(InvoiceSummaryEntity entity) throws FileNotFoundException;

}
