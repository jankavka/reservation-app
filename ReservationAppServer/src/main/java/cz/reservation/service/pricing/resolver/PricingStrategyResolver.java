package cz.reservation.service.pricing.resolver;

import cz.reservation.constant.PricingType;
import cz.reservation.service.pricing.pricinginterface.PricingEngine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PricingStrategyResolver {

    Map<PricingType, PricingEngine> strategies;

    public PricingStrategyResolver(List<PricingEngine> strategies) {
        this.strategies = strategies
                .stream()
                .collect(Collectors.toMap(
                        PricingEngine::supports,
                        Function.identity()));

    }

    public PricingEngine resolve(PricingType pricingType) {
        return strategies.get(pricingType);
    }
}
