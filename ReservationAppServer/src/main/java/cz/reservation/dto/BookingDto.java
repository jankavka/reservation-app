package cz.reservation.dto;

import cz.reservation.constant.BookingStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingDto(
        Long id,

        @NotNull(message = "Training slot must not be null")
        TrainingSlotDto trainingSlot,

        @NotNull(message = "Player must not be mull")
        PlayerDto player,

        BookingStatus bookingStatus,

        LocalDateTime bookedAt
) {
}
