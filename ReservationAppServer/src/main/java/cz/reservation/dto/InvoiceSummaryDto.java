package cz.reservation.dto;

import cz.reservation.constant.PricingType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.Month;

public record InvoiceSummaryDto(
        Long id,

        @NotNull(message = "User must not be null")
        UserDto user,

        @NotNull(message = "Month must not be null")
        Month month,

        Integer totalCentsAmount,

        @NotNull(message = "Pricing type must not be null")
        PricingType pricingType,

        @Nullable
        PricingRuleDto rule,

        @NotBlank(message = "Currency must not be blank")
        String currency,

        LocalDateTime generatedAt,

        String path
) {
}
