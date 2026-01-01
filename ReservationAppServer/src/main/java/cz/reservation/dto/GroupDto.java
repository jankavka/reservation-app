package cz.reservation.dto;

import cz.reservation.constant.Level;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GroupDto(
        Long id,

        @NotBlank(message = "Name must not by null")
        String name,

        @NotNull(message = "Level must not be null")
        Level level,

        @Nullable
        CoachDto coach,

        @NotNull(message = "Capacity must not be null")
        @Min(value = 1, message = "Minimal capacity is 1")
        Integer capacity,

        @NotNull(message = "Season must not be null")
        SeasonDto season
) {
}
