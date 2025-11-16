package cz.reservation.dto;

import cz.reservation.constant.BookingStatus;

import java.util.Date;

public record BookingDto(
        Long id,
        TrainingSlotDto trainingSlot,
        PlayerDto playerDto,
        BookingStatus bookingStatus,
        Date bookedAt
) {
}
