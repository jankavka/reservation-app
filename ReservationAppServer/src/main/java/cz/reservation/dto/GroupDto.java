package cz.reservation.dto;

import cz.reservation.constant.Level;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GroupDto(
        Long id,

        @NotBlank
        String name,

        @NotNull
        Level level,

        @Nullable
        CoachDto coach,

        @NotNull
        //@Size(max = 4, min = 1, message = "capacity is minimal 1 and maximal 4")
        Integer capacity,

        @NotNull
        SeasonDto season
) {
}
