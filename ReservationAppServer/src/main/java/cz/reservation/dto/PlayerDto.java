package cz.reservation.dto;

import cz.reservation.constant.Handedness;
import cz.reservation.entity.UserEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record PlayerDto(
        Long id,

        @NotBlank
        String fullName,

        @NotNull
        Date birthDate,

        Handedness handedness,

        UserDto parent,

        String note

) {
}
