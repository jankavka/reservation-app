package cz.reservation.entity.repository.specification;

import cz.reservation.entity.InvoiceSummaryEntity;
import cz.reservation.entity.InvoiceSummaryEntity_;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.PlayerEntity_;
import cz.reservation.entity.filter.InvoiceSummaryFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class InvoiceSummarySpecification implements Specification<InvoiceSummaryEntity> {

    private final transient InvoiceSummaryFilter invoiceSummaryFilter;

    @Override
    public Predicate toPredicate(Root<InvoiceSummaryEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (invoiceSummaryFilter.playerId() != null) {
            Join<PlayerEntity, InvoiceSummaryEntity> playerJoin = root.join(InvoiceSummaryEntity_.PLAYER);
            predicates.add(criteriaBuilder.equal(playerJoin.get(PlayerEntity_.ID), invoiceSummaryFilter.playerId()));
        }

        if (invoiceSummaryFilter.month() != null) {
            predicates.add(criteriaBuilder.equal(root.get(InvoiceSummaryEntity_.MONTH), invoiceSummaryFilter.month()));
        }

        if (invoiceSummaryFilter.amountOfCentsBiggerThan() != null) {
            predicates.add(criteriaBuilder
                    .greaterThan(root.get(InvoiceSummaryEntity_.TOTAL_CENTS_AMOUNT), invoiceSummaryFilter.amountOfCentsBiggerThan()));
        }

        if (invoiceSummaryFilter.amountOfCentsLessThan() != null) {
            predicates.add(criteriaBuilder
                    .lessThan(root.get(InvoiceSummaryEntity_.TOTAL_CENTS_AMOUNT), invoiceSummaryFilter.amountOfCentsLessThan()));
        }

        if (invoiceSummaryFilter.createdAfter() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(InvoiceSummaryEntity_.GENERATED_AT), invoiceSummaryFilter.createdAfter()));
        }

        if (invoiceSummaryFilter.createdBefore() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(InvoiceSummaryEntity_.GENERATED_AT), invoiceSummaryFilter.createdBefore()));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
