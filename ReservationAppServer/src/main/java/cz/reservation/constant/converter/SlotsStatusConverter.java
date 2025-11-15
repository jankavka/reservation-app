package cz.reservation.constant.converter;

import cz.reservation.constant.SlotsStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class SlotsStatusConverter implements AttributeConverter<SlotsStatus, String> {

    @Override
    public String convertToDatabaseColumn(SlotsStatus slotsStatus) {
        if (slotsStatus == null) {
            return null;
        }
        return slotsStatus.getCode();
    }

    @Override
    public SlotsStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(SlotsStatus.values())
                .filter(status -> status.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
