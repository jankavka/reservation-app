package cz.reservation.dto;

import cz.reservation.constant.WeatherCondition;
import jakarta.validation.constraints.NotNull;

public record WeatherNotesDto(
        Long id,

        @NotNull
        TrainingSlotDto trainingSlot,

        WeatherCondition weatherCondition,

        String note
) {
}
