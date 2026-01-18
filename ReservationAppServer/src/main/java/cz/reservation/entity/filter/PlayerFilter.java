package cz.reservation.entity.filter;

import cz.reservation.constant.Handedness;
import cz.reservation.constant.PricingType;

import java.time.LocalDateTime;

public record PlayerFilter(
        String name,
        LocalDateTime birthAfter,
        LocalDateTime birthBefore,
        Handedness handedness,
        Long parentId,
        PricingType pricingType,
        String note
) {
}
