package cz.reservation.service.invoice;

import cz.reservation.entity.InvoiceSummaryEntity;
import cz.reservation.entity.PackageEntity;

import java.io.FileNotFoundException;


public interface InvoiceEngine {

    String createInvoice(InvoiceSummaryEntity entity) throws FileNotFoundException;

    String createInvoiceForPackage(PackageEntity entity) throws FileNotFoundException;

}
