package cz.reservation.service.invoice;

import cz.reservation.entity.InvoiceSummaryEntity;
import cz.reservation.entity.PackageEntity;

import java.io.IOException;

public interface InvoiceEngine {

    String createInvoice(InvoiceSummaryEntity entity) throws IOException;

    String createInvoice(PackageEntity entity) throws IOException;

}
