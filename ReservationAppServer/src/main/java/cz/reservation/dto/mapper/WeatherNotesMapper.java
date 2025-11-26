package cz.reservation.dto.mapper;

import cz.reservation.dto.WeatherNotesDto;
import cz.reservation.entity.WeatherNotesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WeatherNotesMapper {

    WeatherNotesEntity toEntity(WeatherNotesDto weatherNotesDto);

    WeatherNotesDto toDto(WeatherNotesEntity weatherNotesEntity);

    void updateEntity(@MappingTarget WeatherNotesEntity target, WeatherNotesDto source);
}
