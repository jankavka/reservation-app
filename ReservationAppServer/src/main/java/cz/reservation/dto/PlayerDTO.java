package cz.reservation.dto;

import cz.reservation.constant.Handedness;

import java.util.Date;

public record PlayerDTO(
        Long id,
        String fullName,
        Date birthDate,
        Handedness handedness,
        Long parentUserId,
        String note

) {
}
