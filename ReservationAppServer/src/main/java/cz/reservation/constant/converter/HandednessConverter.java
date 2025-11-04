package cz.reservation.constant.converter;

import cz.reservation.constant.Handedness;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class HandednessConverter implements AttributeConverter<Handedness, String> {


    @Override
    public String convertToDatabaseColumn(Handedness handedness) {
        if (handedness == null) {
            return null;
        }
        return handedness.getHand();
    }

    @Override
    public Handedness convertToEntityAttribute(String hand) {
        if (hand == null) {
            return null;
        }

        return Stream.of(Handedness.values())
                .filter(h -> h.getHand().equals(hand))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
