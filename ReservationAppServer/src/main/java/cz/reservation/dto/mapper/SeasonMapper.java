package cz.reservation.dto.mapper;

import cz.reservation.dto.SeasonDto;
import cz.reservation.entity.SeasonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SeasonMapper {

    SeasonDto toDto(SeasonEntity seasonEntity);

    SeasonEntity toEntity(SeasonDto seasonDto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget SeasonEntity target, SeasonDto source);
}
