package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CoachDto(
        Long id,

        @NotNull(message = "User must not be null")
        UserDto user,

        @NotBlank(message = "This field can not be blank")
        String bio,

        @NotBlank(message = "This field can not be blank")
        String certification,

        List<GroupDto> groups
) {
}
