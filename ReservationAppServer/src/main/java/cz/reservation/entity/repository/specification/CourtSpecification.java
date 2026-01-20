package cz.reservation.entity.repository.specification;

import cz.reservation.entity.CourtEntity;
import cz.reservation.entity.CourtEntity_;
import cz.reservation.entity.VenueEntity;
import cz.reservation.entity.VenueEntity_;
import cz.reservation.entity.filter.CourtFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CourtSpecification implements Specification<CourtEntity> {

    private final transient CourtFilter courtFilter;

    @Override
    public Predicate toPredicate(Root<CourtEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (courtFilter.venueId() != null) {
            Join<VenueEntity, CourtEntity> venueJoin = root.join(CourtEntity_.VENUE);
            predicates.add(criteriaBuilder.equal(venueJoin.get(VenueEntity_.ID), courtFilter.venueId()));
        }

        if (courtFilter.name() != null) {
            predicates.add(criteriaBuilder
                    .like(root.get(CourtEntity_.NAME), "%" + courtFilter.name() + "%"));
        }

        if (courtFilter.surface() != null) {
            predicates.add(criteriaBuilder.equal(root.get(CourtEntity_.SURFACE), courtFilter.surface()));
        }

        if (courtFilter.indoor() != null) {
            predicates.add(criteriaBuilder.equal(root.get(CourtEntity_.INDOOR), courtFilter.indoor()));
        }

        if (courtFilter.lighting() != null) {
            predicates.add(criteriaBuilder.equal(root.get(CourtEntity_.LIGHTING), courtFilter.lighting()));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
