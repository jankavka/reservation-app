package cz.reservation.dto.mapper;

import cz.reservation.dto.WeatherNotesDto;
import cz.reservation.entity.WeatherNotesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WeatherNotesMapper {

    WeatherNotesEntity toEntity(WeatherNotesDto weatherNotesDto);

    WeatherNotesDto toDto(WeatherNotesEntity weatherNotesEntity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget WeatherNotesEntity target, WeatherNotesDto source);
}
