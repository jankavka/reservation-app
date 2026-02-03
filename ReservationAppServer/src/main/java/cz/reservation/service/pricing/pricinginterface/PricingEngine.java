package cz.reservation.service.pricing.pricinginterface;

import cz.reservation.constant.PricingType;
import cz.reservation.dto.InvoiceSummaryDto;


public interface PricingEngine {

    PricingType supports();

    Integer computePrice(InvoiceSummaryDto invoiceSummaryDto);

}
