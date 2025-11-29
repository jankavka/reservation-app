package cz.reservation.dto;

import cz.reservation.constant.Handedness;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PlayerDto(
        Long id,

        @NotBlank(message = "Name must not be empty")
        String fullName,

        @NotNull(message = "Birth date must not be null")
        LocalDateTime birthDate,

        @NotNull(message = "Handedness must not be null")
        Handedness handedness,

        @NotNull(message = "Parent must not be null")
        UserDto parent,

        String note

) {
}
