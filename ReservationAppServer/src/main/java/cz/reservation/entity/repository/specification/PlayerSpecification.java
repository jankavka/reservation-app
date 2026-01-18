package cz.reservation.entity.repository.specification;

import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.PlayerEntity_;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.UserEntity_;
import cz.reservation.entity.filter.PlayerFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PlayerSpecification implements Specification<PlayerEntity> {

    private final PlayerFilter playerFilter;


    @Override
    public Predicate toPredicate(Root<PlayerEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (playerFilter.name() != null) {
            predicates.add(criteriaBuilder
                    .like(root.get(PlayerEntity_.FULL_NAME), "%" + playerFilter.name() + "%"));
        }

        if (playerFilter.birthAfter() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(PlayerEntity_.BIRTH_DATE), playerFilter.birthAfter()));
        }

        if (playerFilter.birthBefore() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(PlayerEntity_.BIRTH_DATE), playerFilter.birthBefore()));
        }

        if (playerFilter.handedness() != null) {
            predicates.add(criteriaBuilder
                    .equal(root.get(PlayerEntity_.HANDEDNESS), playerFilter.handedness()));
        }

        if (playerFilter.parentId() != null) {
            Join<UserEntity, PlayerEntity> parentJoin = root.join(UserEntity_.ID);
            predicates.add(criteriaBuilder.equal(parentJoin, playerFilter.parentId()));
        }

        if (playerFilter.pricingType() != null) {
            predicates.add(criteriaBuilder
                    .equal(root.get(PlayerEntity_.PRICING_TYPE), playerFilter.pricingType()));
        }

        if (playerFilter.note() != null) {
            predicates.add(criteriaBuilder
                    .like(root.get(PlayerEntity_.NOTE), "%" + playerFilter.note() + "%"));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
