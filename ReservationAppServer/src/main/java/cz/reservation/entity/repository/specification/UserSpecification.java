package cz.reservation.entity.repository.specification;

import cz.reservation.entity.UserEntity;
import cz.reservation.entity.UserEntity_;
import cz.reservation.entity.filter.UserFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserSpecification implements Specification<UserEntity> {

    private final UserFilter userFilter;

    @Override
    public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (userFilter.email() != null) {
            predicates.add(criteriaBuilder.like(root.get(UserEntity_.EMAIL), "%" + userFilter.email() + "%"));
        }

        if (userFilter.fullName() != null) {
            predicates.add(criteriaBuilder.like(root.get(UserEntity_.FULL_NAME), "%" + userFilter.fullName() + "%"));
        }

        if (userFilter.telephoneNumber() != null) {
            predicates.add(criteriaBuilder
                    .like(root.get(UserEntity_.TELEPHONE_NUMBER), "%" + userFilter.telephoneNumber() + "%"));
        }

        if (userFilter.roles() != null) {
            Expression<String> rolesString = root.join(UserEntity_.ROLES);
            predicates.add(rolesString.in(userFilter.roles()));
        }

        if (userFilter.createdAfter() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(UserEntity_.CREATED_AT), userFilter.createdAfter()));
        }

        if (userFilter.createdBefore() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(UserEntity_.CREATED_AT), userFilter.createdBefore()));
        }

        if (userFilter.isCoach() != null && userFilter.isCoach()) {
            Expression<Boolean> isCoach = root.get(UserEntity_.COACH).isNotNull();
            predicates.add(criteriaBuilder.isTrue(isCoach));
        }

        if (userFilter.isCoach() != null && !userFilter.isCoach()) {
            Expression<Boolean> isNotCoach = root.get(UserEntity_.COACH).isNull();
            predicates.add(criteriaBuilder.isTrue(isNotCoach));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
