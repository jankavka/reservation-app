package cz.reservation.service.invoice;

import cz.reservation.entity.InvoiceSummaryEntity;

import java.io.IOException;

public interface InvoiceEngine {

    String createInvoice(InvoiceSummaryEntity entity) throws IOException;

}
