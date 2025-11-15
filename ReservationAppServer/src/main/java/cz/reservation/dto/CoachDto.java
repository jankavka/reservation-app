package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CoachDto(
        Long id,

        UserDto userId,

        @NotBlank(message = "This field can not be blank")
        String bio,

        @NotBlank(message = "This field can not be blank")
        String certification,

        List<GroupDto> groups
) {
}
