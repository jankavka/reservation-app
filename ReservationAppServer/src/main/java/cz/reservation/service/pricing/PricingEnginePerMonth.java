package cz.reservation.service.pricing;

import cz.reservation.constant.PricingType;
import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.service.pricing.pricinginterface.PricingEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PricingEnginePerMonth implements PricingEngine {
    @Override
    public PricingType supports() {
        return PricingType.MONTHLY;
    }

    @Override
    public Integer computePrice(InvoiceSummaryDto invoiceSummaryDto) {
        var rule = invoiceSummaryDto.rule();
        if (rule != null && rule.pricingType().equals(PricingType.MONTHLY)) {
            return rule.amountCents();
        } else {
            throw new NullPointerException("If pricing type is MONTHLY rule can not be null");
        }
    }
}
