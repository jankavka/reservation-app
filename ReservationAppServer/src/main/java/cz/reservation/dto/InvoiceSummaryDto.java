package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvoiceSummaryDto(
        Long id,

        @NotNull
        UserDto user,

        @NotNull
        LocalDate month,

        @NotNull
        Double totalAmount,

        @NotBlank
        String currency,

        LocalDateTime generatedAt
) {
}
