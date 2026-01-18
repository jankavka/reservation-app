package cz.reservation.entity.repository.specification;

import cz.reservation.entity.TrainingSlotEntity;
import cz.reservation.entity.filter.TrainingSlotFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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


        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
