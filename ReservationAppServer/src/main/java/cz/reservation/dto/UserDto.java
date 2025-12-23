package cz.reservation.dto;

import cz.reservation.constant.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Set;


public record UserDto(
        Long id,

        @NotNull
        String email,

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
