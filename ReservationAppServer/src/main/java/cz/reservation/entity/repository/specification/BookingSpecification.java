package cz.reservation.entity.repository.specification;

import cz.reservation.entity.*;
import cz.reservation.entity.filter.BookingFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BookingSpecification implements Specification<BookingEntity> {

    private final transient BookingFilter bookingFilter;

    @Override
    public Predicate toPredicate(Root<BookingEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (bookingFilter.bookingStatus() != null) {
            predicates.add(criteriaBuilder
                    .equal(root.get(BookingEntity_.BOOKING_STATUS), bookingFilter.bookingStatus()));
        }

        if (bookingFilter.createdAfter() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(BookingEntity_.BOOKED_AT), bookingFilter.createdAfter()));
        }

        if (bookingFilter.createdBefore() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(BookingEntity_.BOOKED_AT), bookingFilter.createdBefore()));
        }

        if (bookingFilter.playerId() != null) {
            Join<PlayerEntity, BookingEntity> playerJoin = root.join(BookingEntity_.PLAYER);
            predicates.add(criteriaBuilder.equal(playerJoin.get(PlayerEntity_.ID), bookingFilter.playerId()));
        }

        if (bookingFilter.trainingSlotId() != null) {
            Join<TrainingSlotEntity, BookingEntity> trainingSlotJoin = root.join(BookingEntity_.TRAINING_SLOT);
            predicates.add(criteriaBuilder
                    .equal(trainingSlotJoin.get(TrainingSlotEntity_.ID), bookingFilter.trainingSlotId()));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
