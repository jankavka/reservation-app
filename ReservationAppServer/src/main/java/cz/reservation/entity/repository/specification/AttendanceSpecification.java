package cz.reservation.entity.repository.specification;

import cz.reservation.entity.AttendanceEntity;
import cz.reservation.entity.AttendanceEntity_;
import cz.reservation.entity.filter.AttendanceFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AttendanceSpecification implements Specification<AttendanceEntity> {

    private final transient AttendanceFilter attendanceFilter;

    @Override
    public Predicate toPredicate(Root<AttendanceEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (attendanceFilter.present() != null) {
            predicates.add(criteriaBuilder.equal(root.get(AttendanceEntity_.PRESENT), attendanceFilter.present()));
        }

        if (attendanceFilter.createdAfter() != null) {
            predicates.add(criteriaBuilder
                    .greaterThanOrEqualTo(root.get(AttendanceEntity_.PRESENT), attendanceFilter.createdAfter()));
        }

        if (attendanceFilter.createdBefore() != null) {
            predicates.add(criteriaBuilder
                    .lessThanOrEqualTo(root.get(AttendanceEntity_.PRESENT), attendanceFilter.createdBefore()));
        }

        if (attendanceFilter.note() != null) {
            predicates.add(criteriaBuilder
                    .like(root.get(AttendanceEntity_.NOTE), "%" + attendanceFilter.note() + "%"));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
