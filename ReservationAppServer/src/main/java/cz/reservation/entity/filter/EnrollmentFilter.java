package cz.reservation.entity.filter;

import cz.reservation.constant.EnrollmentState;

import java.time.LocalDateTime;

public record EnrollmentFilter(
        Long playerId,
        Long groupId,
        EnrollmentState state,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore
) {
}