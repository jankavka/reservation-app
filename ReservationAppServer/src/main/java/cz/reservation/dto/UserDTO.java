package cz.reservation.dto;

import cz.reservation.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.Set;


public record UserDTO(
        Long id,

        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min = 6)
        String password,

        @NotNull
        String fullName,

        Set<Role> roles,

        Date createdAt
) {
}
