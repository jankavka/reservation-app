package cz.reservation.constant.converter;

import cz.reservation.constant.Surface;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class SurfaceConverter implements AttributeConverter<Surface, String> {
    @Override
    public String convertToDatabaseColumn(Surface surface) {
        if (surface == null){
            return null;
        }
        return surface.getCode();
    }

    @Override
    public Surface convertToEntityAttribute(String code) {
        if(code == null){
            return null;
        }

        return Stream.of(Surface.values())
                .filter(surface -> surface.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
