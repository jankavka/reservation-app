package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.Month;

public record InvoiceSummaryDto(
        Long id,

        @NotNull
        UserDto user,

        @NotNull
        Month month,

        Double totalAmount,

        @NotBlank
        String currency,

        LocalDateTime generatedAt
) {
}
