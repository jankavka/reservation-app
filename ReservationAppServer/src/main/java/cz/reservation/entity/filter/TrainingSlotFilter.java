package cz.reservation.entity.filter;

import cz.reservation.constant.SlotStatus;

import java.time.LocalDateTime;

public record TrainingSlotFilter(
        Long groupId,
        Long courtId,
        LocalDateTime startsAfter,
        LocalDateTime startsBefore,
        Integer latsInMinutes,
        Integer capacity,
        SlotStatus status

) {
}
