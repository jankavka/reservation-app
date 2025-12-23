package cz.reservation.service.pricing;

import cz.reservation.constant.PricingType;
import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.service.pricing.pricinginterface.PricingEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PricingEnginePerPackage implements PricingEngine {

    @Override
    public PricingType supports() {
        return PricingType.PACKAGE;
    }

    @Override
    public Integer computePrice(InvoiceSummaryDto invoiceSummaryDto) {
        return 0;
    }
}
