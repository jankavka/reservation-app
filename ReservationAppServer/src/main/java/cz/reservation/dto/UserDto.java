package cz.reservation.dto;

import cz.reservation.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.Set;


public record UserDto(
        Long id,

        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min = 6, message = "Password has to have minimum 6 characters")
        String password,

        @NotNull
        String fullName,

        Set<Role> roles,

        Date createdAt
) {
}
