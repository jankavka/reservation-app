package cz.reservation.constant.converter;

import cz.reservation.constant.EnrollmentState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class EnrollmentStateConverter implements AttributeConverter<EnrollmentState, String> {

    @Override
    public String convertToDatabaseColumn(EnrollmentState enrollmentState) {
        if (enrollmentState == null) {
            return null;
        }
        return enrollmentState.getCode();
    }

    @Override
    public EnrollmentState convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(EnrollmentState.values())
                .filter(state -> state.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
