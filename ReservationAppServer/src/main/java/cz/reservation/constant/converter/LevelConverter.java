package cz.reservation.constant.converter;

import cz.reservation.constant.Level;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class LevelConverter implements AttributeConverter<Level, String> {


    @Override
    public String convertToDatabaseColumn(Level level) {
        if(level == null){
            return null;
        }
        return level.getActualLevel();
    }

    @Override
    public Level convertToEntityAttribute(String actualLevel) {
        if(actualLevel == null){
            return null;
        }
        return Stream.of(Level.values())
                .filter(l -> l.getActualLevel().equals(actualLevel))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
