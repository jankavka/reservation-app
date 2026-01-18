package cz.reservation.entity.filter;

import cz.reservation.constant.BookingStatus;

import java.time.LocalDateTime;

public record BookingFilter(
        Long trainingSlotId,
        Long playerId,
        BookingStatus bookingStatus,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore
) {
}
