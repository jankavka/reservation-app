package cz.reservation.dto.mapper;

import cz.reservation.dto.CourtDto;
import cz.reservation.entity.CourtEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourtMapper {

    CourtDto toDto(CourtEntity courtEntity);

    CourtEntity toEntity(CourtDto courtDto);

}
