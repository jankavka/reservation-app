package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Date;

public record SeasonDto(
        Long id,

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull(message = "Filed must not be blank")
        LocalDateTime dateFrom,

        @NotNull(message = "Field must not be blank")
        LocalDateTime dateUntil
) {
}
