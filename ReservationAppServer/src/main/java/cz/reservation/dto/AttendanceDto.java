package cz.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AttendanceDto(
        Long id,

        @NotNull(message = "Booking must not be null")
        BookingDto booking,

        Boolean present,

        String note,

        LocalDateTime createdAt
) {

}
