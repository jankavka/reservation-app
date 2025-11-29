package cz.reservation.dto;

import cz.reservation.constant.EnrollmentState;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Date;

public record EnrollmentDto(
        Long id,

        @NotNull(message = "Player must not be null")
        PlayerDto player,

        @NotNull(message = "Group must not be null")
        GroupDto group,

        EnrollmentState state,

        LocalDateTime createdAt
) {
}
