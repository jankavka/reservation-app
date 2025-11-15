package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record InvoiceSummaryDto(
        Long id,

        @NotNull
        UserDto user,

        @NotNull
        Date month,

        @NotNull
        Double totalAmount,

        @NotBlank
        String currency,

        @NotNull
        Date generatedAt
) {
}
