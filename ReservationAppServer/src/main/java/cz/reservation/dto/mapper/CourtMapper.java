package cz.reservation.dto.mapper;

import cz.reservation.dto.CourtDto;
import cz.reservation.entity.CourtEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourtMapper {

    CourtDto toDto(CourtEntity courtEntity);

    CourtEntity toEntity(CourtDto courtDto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget CourtEntity target, CourtDto source);

}
