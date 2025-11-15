package cz.reservation.dto;

import cz.reservation.constant.Level;
import cz.reservation.entity.SeasonEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GroupDto(
        Long id,

        @NotBlank
        String name,

        @NotNull
        Level level,

        @NotNull
        CoachDto coach,

        @NotNull
        Integer capacity,

        @NotNull
        SeasonEntity season
) {
}
