package cz.reservation.constant.converter;

import cz.reservation.constant.SlotStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class SlotStatusConverter implements AttributeConverter<SlotStatus, String> {

    @Override
    public String convertToDatabaseColumn(SlotStatus slotsStatus) {
        if (slotsStatus == null) {
            return null;
        }
        return slotsStatus.getCode();
    }

    @Override
    public SlotStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(SlotStatus.values())
                .filter(status -> status.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
