package cz.reservation.entity.filter;

import java.time.LocalDateTime;
import java.time.Month;

public record InvoiceSummaryFilter(
        Long playerId,
        Month month,
        Integer amountOfCentsBiggerThan,
        Integer amountOfCentsLessThan,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore
) {
}
