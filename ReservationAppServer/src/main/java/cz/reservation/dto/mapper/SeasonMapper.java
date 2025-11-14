package cz.reservation.dto.mapper;

import cz.reservation.dto.SeasonDto;
import cz.reservation.entity.SeasonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeasonMapper {

    SeasonDto toDto(SeasonEntity seasonEntity);

    SeasonEntity toEntity(SeasonDto seasonDto);
}
