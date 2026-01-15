package cz.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationRequestDto(

        @NotNull(message = "Email must not be null")
        @Email(message = "Email must be in right format")
        String email,

        @NotNull
        @Size(min = 6, message = "Password has to have minimum 6 characters")
        String password,

        @NotNull
        @Size(
                min = 10,
                message = "Telephone number must contain country code (+420) and has to have at least 10 characters ")
        String telephoneNumber,

        @NotNull
        String fullName
) {
}
