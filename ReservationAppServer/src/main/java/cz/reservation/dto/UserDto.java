package cz.reservation.dto;

import cz.reservation.constant.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Set;


public record UserDto(
        Long id,

        @NotNull
        @Email
        String email,

        @Size(
                min = 10,
                message = "Telephone number must contain country code (+420) and has to have at least 10 characters ")
        String telephoneNumber,

        @NotNull
        String fullName,

        @NotEmpty(message = "Roles must not be empty")
        Set<Role> roles,

        //List<PlayerDto> players,

        //List<InvoiceSummaryDto> invoices,

        LocalDateTime createdAt
) {
}
