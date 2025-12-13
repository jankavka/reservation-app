package cz.reservation.dto;

import cz.reservation.constant.PricingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record PricingRuleDto(
        Long id,

        @NotBlank(message = "Name of rule must not be blank")
        String name,

        @NotNull(message = "Pricing type must not be null")
        PricingType pricingType,

        @NotNull(message = "Amount must not be null")
        Integer amountCents,

        @NotNull(message = "currency must not be null")
        String currency,

        Map<String, Object> conditions











) {
}
