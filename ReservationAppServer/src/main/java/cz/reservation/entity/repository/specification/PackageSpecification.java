package cz.reservation.entity.repository.specification;

import cz.reservation.entity.PackageEntity;
import cz.reservation.entity.PackageEntity_;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.PlayerEntity_;
import cz.reservation.entity.filter.PackageFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PackageSpecification implements Specification<PackageEntity> {

    private final transient PackageFilter packageFilter;

    @Override
    public Predicate toPredicate(Root<PackageEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (packageFilter.playerId() != null) {
            Join<PlayerEntity, PackageEntity> playerJoin = root.join(PackageEntity_.PLAYER);
            predicates.add(criteriaBuilder.equal(playerJoin.get(PlayerEntity_.ID), packageFilter.playerId()));
        }

        if (packageFilter.name() != null) {
            predicates.add(criteriaBuilder
                    .like(root.get(PackageEntity_.NAME), "%" + packageFilter.name() + "%"));
        }

        if (packageFilter.validAfter() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(PackageEntity_.VALID_TO), packageFilter.validAfter()));
        }

        if (packageFilter.validBefore() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(PackageEntity_.VALID_FROM), packageFilter.validBefore()));
        }

        if (packageFilter.hasAvailableSlots() != null && packageFilter.hasAvailableSlots()) {
            predicates.add(criteriaBuilder
                    .greaterThan(root.get(PackageEntity_.AVAILABLE_SLOTS), 0));
        }

        if (packageFilter.availableSlotsMin() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(PackageEntity_.AVAILABLE_SLOTS), packageFilter.availableSlotsMin()));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
