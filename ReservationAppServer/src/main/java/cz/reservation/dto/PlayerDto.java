package cz.reservation.dto;

import cz.reservation.constant.Handedness;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record PlayerDto(
        Long id,

        @NotBlank(message = "Name must not be empty")
        String fullName,

        @NotNull
        Date birthDate,

        @NotNull
        Handedness handedness,

        UserDto parent,

        String note

) {
}
