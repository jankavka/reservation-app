package cz.reservation.dto;

import cz.reservation.constant.Role;

import java.util.Date;
import java.util.Set;

public record UserDTO(
        Long id,
        String email,
        String password,
        String fullName,
        Set<Role> roles,
        Date createdAt
) {
}
