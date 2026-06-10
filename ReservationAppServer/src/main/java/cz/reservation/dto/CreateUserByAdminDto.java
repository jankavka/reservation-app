package cz.reservation.dto;

import cz.reservation.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUserByAdminDto(
        @NotNull(message = "Email must not be null")
        @Email(message = "Email must be in right format")
        String email,

        @NotNull
        @Size(
                min = 10,
                message = "Telephone number must contain country code (+420) and has to have at least 10 characters ")
        String telephoneNumber,

        @NotNull
        String fullName,

        @NotEmpty(message = "Roles must not be empty")
        Set<Role> roles

) {
}
