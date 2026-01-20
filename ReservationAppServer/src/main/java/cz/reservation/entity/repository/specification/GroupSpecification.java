package cz.reservation.entity.repository.specification;

import cz.reservation.entity.CoachEntity;
import cz.reservation.entity.CoachEntity_;
import cz.reservation.entity.GroupEntity;
import cz.reservation.entity.GroupEntity_;
import cz.reservation.entity.SeasonEntity;
import cz.reservation.entity.SeasonEntity_;
import cz.reservation.entity.filter.GroupFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GroupSpecification implements Specification<GroupEntity> {

    private final transient GroupFilter groupFilter;

    @Override
    public Predicate toPredicate(Root<GroupEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (groupFilter.coachId() != null) {
            Join<CoachEntity, GroupEntity> coachJoin = root.join(GroupEntity_.COACH);
            predicates.add(criteriaBuilder.equal(coachJoin.get(CoachEntity_.ID), groupFilter.coachId()));
        }

        if (groupFilter.seasonId() != null) {
            Join<SeasonEntity, GroupEntity> seasonJoin = root.join(GroupEntity_.SEASON);
            predicates.add(criteriaBuilder.equal(seasonJoin.get(SeasonEntity_.ID), groupFilter.seasonId()));
        }

        if (groupFilter.name() != null) {
            predicates.add(criteriaBuilder
                    .like(root.get(GroupEntity_.NAME), "%" + groupFilter.name() + "%"));
        }

        if (groupFilter.capacityMin() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(GroupEntity_.CAPACITY), groupFilter.capacityMin()));
        }

        if (groupFilter.capacityMax() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(GroupEntity_.CAPACITY), groupFilter.capacityMax()));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
