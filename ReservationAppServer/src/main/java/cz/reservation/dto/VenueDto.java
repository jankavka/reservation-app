package cz.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VenueDto(
        Long id,

        @NotBlank(message = "Name of the venue must not be empty")
        String name,

        @NotBlank(message = "Address of the venue must not be empty")
        String address,

        @NotBlank(message = "Phone number must not be empty")
        @Size(min = 9, message = "Phone number has to have 9 numbers at least")
        String phoneNumber
) {
}
