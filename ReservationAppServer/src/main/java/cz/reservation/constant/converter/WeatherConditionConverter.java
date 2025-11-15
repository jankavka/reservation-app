package cz.reservation.constant.converter;

import cz.reservation.constant.WeatherCondition;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class WeatherConditionConverter implements AttributeConverter<WeatherCondition, String> {
    @Override
    public String convertToDatabaseColumn(WeatherCondition weatherCondition) {
        if (weatherCondition == null) {
            return null;
        }
        return weatherCondition.getCode();
    }

    @Override
    public WeatherCondition convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(WeatherCondition.values())
                .filter(condition -> condition.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
