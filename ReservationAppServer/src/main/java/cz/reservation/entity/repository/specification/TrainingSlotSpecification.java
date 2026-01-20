package cz.reservation.entity.repository.specification;

import cz.reservation.entity.*;
import cz.reservation.entity.filter.TrainingSlotFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TrainingSlotSpecification implements Specification<TrainingSlotEntity> {

    private final TrainingSlotFilter trainingSlotFilter;

    @Override
    public Predicate toPredicate(Root<TrainingSlotEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (trainingSlotFilter.groupId() != null) {
            Join<GroupEntity, TrainingSlotEntity> groupJoin = root.join(TrainingSlotEntity_.GROUP);
            predicates.add(criteriaBuilder.equal(groupJoin.get(GroupEntity_.ID), TrainingSlotEntity_.ID));
        }

        if (trainingSlotFilter.courtId() != null) {
            Join<CourtEntity, TrainingSlotEntity> courtJoin = root.join(TrainingSlotEntity_.COURT);
            predicates.add(criteriaBuilder.equal(courtJoin.get(CourtEntity_.ID), trainingSlotFilter.courtId()));
        }

        if (trainingSlotFilter.startsAfter() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(TrainingSlotEntity_.START_AT), trainingSlotFilter.startsAfter()));
        }

        if (trainingSlotFilter.startsBefore() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(TrainingSlotEntity_.START_AT), trainingSlotFilter.startsBefore()));
        }

        if (trainingSlotFilter.capacity() != null) {
            predicates.add(criteriaBuilder
                    .equal(root.get(TrainingSlotEntity_.CAPACITY), trainingSlotFilter.capacity()));
        }

        if (trainingSlotFilter.status() != null) {
            predicates.add(criteriaBuilder.equal(root.get(TrainingSlotEntity_.STATUS), trainingSlotFilter.status()));
        }


        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
