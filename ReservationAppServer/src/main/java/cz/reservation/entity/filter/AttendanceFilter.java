package cz.reservation.entity.filter;

import java.time.LocalDateTime;

public record AttendanceFilter(
        Boolean present,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore,
        String note

) {
}
