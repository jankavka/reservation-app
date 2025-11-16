package cz.reservation.dto;

public record AttendanceDto(
        Long id,
        BookingDto booking,
        Boolean present,
        String note
) {

}
