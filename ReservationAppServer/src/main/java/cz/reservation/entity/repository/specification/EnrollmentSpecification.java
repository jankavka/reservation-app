package cz.reservation.entity.repository.specification;

import cz.reservation.constant.EnrollmentState;
import cz.reservation.entity.*;
import cz.reservation.entity.filter.EnrollmentFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class EnrollmentSpecification implements Specification<EnrollmentEntity> {

    private final transient EnrollmentFilter enrollmentFilter;

    @Override
    public Predicate toPredicate(Root<EnrollmentEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (enrollmentFilter.playerId() != null) {
            Join<PlayerEntity, EnrollmentEntity> playerJoin = root.join(EnrollmentEntity_.PLAYER);
            predicates.add(criteriaBuilder.equal(playerJoin.get(PlayerEntity_.ID), enrollmentFilter.playerId()));
        }

        if (enrollmentFilter.groupId() != null) {
            Join<GroupEntity, EnrollmentEntity> groupJoin = root.join(EnrollmentEntity_.GROUP);
            predicates.add(criteriaBuilder.equal(groupJoin.get(GroupEntity_.ID), enrollmentFilter.groupId()));
        }

        if (enrollmentFilter.state() != null) {
            predicates.add(criteriaBuilder.equal(root.get(EnrollmentEntity_.STATE), enrollmentFilter.state()));
        }

        if (enrollmentFilter.createdBefore() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(EnrollmentEntity_.CREATED_AT), enrollmentFilter.createdAfter()));
        }

        if (enrollmentFilter.createdBefore() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(EnrollmentEntity_.CREATED_AT), enrollmentFilter.createdBefore()));
        }


        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
