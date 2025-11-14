package cz.reservation.dto;

import cz.reservation.constant.Surface;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourtDto(
        Long id,

        @NotBlank(message = "Name can not be blank")
        String name,

        @NotNull(message = "Surface can not be blank")
        Surface surface,

        @NotNull(message = "Surface must be chosen")
        Boolean indoor,

        @NotNull(message = "Lightning must be chosen")
        Boolean lighting
) {
}
