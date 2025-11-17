package cz.reservation.dto;

import cz.reservation.constant.Role;
import jakarta.validation.constraints.*;

import java.util.Date;
import java.util.List;
import java.util.Set;


public record UserDto(
        Long id,

        @NotNull
        @Email
        String email,

        @NotNull
        String fullName,

        @NotEmpty(message = "Roles must not be empty")
        Set<Role> roles,

        List<PlayerDto> players,

        List<InvoiceSummaryDto> invoices,

        Date createdAt
) {
}
