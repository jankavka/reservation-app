package cz.reservation.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.Month;

public record InvoiceSummaryDto(
        Long id,

        @NotNull(message = "Player must not be null")
        PlayerDto player,

        @NotNull(message = "Month must not be null")
        Month month,

        Integer totalCentsAmount,

        @Nullable
        PricingRuleDto rule,

        @NotBlank(message = "Currency must not be blank")
        String currency,

        LocalDateTime generatedAt,

        String path
) {
}
