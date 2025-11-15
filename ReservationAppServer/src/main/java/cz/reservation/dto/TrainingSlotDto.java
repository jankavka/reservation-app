package cz.reservation.dto;

import cz.reservation.constant.SlotsStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record TrainingSlotDto(
        Long id,

        @NotNull(message = "Group must not be null")
        GroupDto group,

        @NotNull(message = "Court must not be null")
        CourtDto court,

        @NotNull(message = "Starting date must not be null")
        Date startAt,

        @NotNull(message = "Ending date must not be null")
        Date endAt,

        @NotNull(message = "Capacity must not be null")
        @Size(min = 1, message = "Minimal capacity is 1")
        Integer capacity,

        @NotNull(message = "Status must not be null")
        SlotsStatus status,

        String price,

        @NotBlank(message = "Curency must not be null")
        String currency
) {
}
