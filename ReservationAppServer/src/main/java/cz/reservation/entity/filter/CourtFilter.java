package cz.reservation.entity.filter;

import cz.reservation.constant.Surface;

public record CourtFilter(
        Long venueId,
        String name,
        Surface surface,
        Boolean indoor,
        Boolean lighting
) {
}
