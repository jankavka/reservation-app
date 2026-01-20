package cz.reservation.entity.filter;

public record GroupFilter(
        Long coachId,
        Long seasonId,
        String name,
        Integer capacityMin,
        Integer capacityMax
) {
}
