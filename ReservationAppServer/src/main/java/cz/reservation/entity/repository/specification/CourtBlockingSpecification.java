package cz.reservation.entity.repository.specification;

import cz.reservation.entity.CourtBlockingEntity;
import cz.reservation.entity.CourtBlockingEntity_;
import cz.reservation.entity.CourtEntity;
import cz.reservation.entity.CourtEntity_;
import cz.reservation.entity.filter.CourtBlockingFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CourtBlockingSpecification implements Specification<CourtBlockingEntity> {

    private final transient CourtBlockingFilter courtBlockingFilter;

    @Override
    public Predicate toPredicate(Root<CourtBlockingEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (courtBlockingFilter.courtId() != null) {
            Join<CourtEntity, CourtBlockingEntity> joinCourt = root.join(CourtBlockingEntity_.COURT);
            predicates.add(criteriaBuilder.equal(joinCourt.get(CourtEntity_.ID), courtBlockingFilter.courtId()));

        }

        if (courtBlockingFilter.reason() != null) {
            predicates.add(criteriaBuilder
                    .like(root.get(CourtBlockingEntity_.REASON), "%" + courtBlockingFilter.reason() + "%"));
        }


        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
