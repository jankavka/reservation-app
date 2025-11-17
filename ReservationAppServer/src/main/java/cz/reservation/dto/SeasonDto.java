package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public record SeasonDto(
        Long id,

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull(message = "Filed must not be blank")
        Date dateFrom,

        @NotNull(message = "Field must not be blank")
        Date dateUntil
) {
}
