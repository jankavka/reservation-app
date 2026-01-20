package cz.reservation.entity.filter;

import java.time.LocalDate;

public record PackageFilter(
        Long playerId,
        String name,
        LocalDate validAfter,
        LocalDate validBefore,
        Boolean hasAvailableSlots,
        Integer availableSlotsMin
) {
}
