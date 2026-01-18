package cz.reservation.entity.filter;

public record CourtBlockingFilter(
        Long courtId,
        Boolean moreThanHour,
        String reason
) {
}
