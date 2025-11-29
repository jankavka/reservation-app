package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public record CourtBlockingDto(
        Long id,

        @NotNull(message = "Court must not be null")
        CourtDto court,

        @NotNull(message = "Date \"blocked from\" must not be null")
        LocalDateTime blockedFrom,

        @NotNull(message = "Date \"blocked to\" must not be null")
        LocalDateTime blockedTo,

        @NotBlank(message = "Reason must not be blank")
        String reason
) {
}
