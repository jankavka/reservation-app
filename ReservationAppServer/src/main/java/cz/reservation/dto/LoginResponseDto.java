package cz.reservation.dto;

public record LoginResponseDto(
        String token,
        long expiresIs
) {
}
