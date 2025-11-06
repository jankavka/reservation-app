package cz.reservation.dto;


public record AuthRequestDTO(
        String username,
        String password
) {
}
