package cz.reservation.dto;

import cz.reservation.constant.SlotStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TrainingSlotDto(
        Long id,

        @NotNull(message = "Group must not be null")
        GroupDto group,

        @NotNull(message = "Court must not be null")
        CourtDto court,

        @NotNull(message = "Starting date must not be null")
        LocalDateTime startAt,

        @NotNull(message = "Ending date must not be null")
        LocalDateTime endAt,

        Integer capacity,

        @NotNull(message = "Status must not be null")
        SlotStatus status,

        String price,

        @NotBlank(message = "Currency must not be null")
        String currency,

        Long courtBlockingId
) {
}
