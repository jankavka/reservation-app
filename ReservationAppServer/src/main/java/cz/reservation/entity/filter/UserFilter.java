package cz.reservation.entity.filter;

import cz.reservation.constant.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record UserFilter(
        String email,
        String telephoneNumber,
        String fullName,
        Set<Role> roles,
        LocalDateTime createdBefore,
        LocalDateTime createdAfter,
        Boolean isCoach

) {
}
