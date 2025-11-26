package cz.reservation.dto.mapper;

import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.entity.CourtBlockingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CourtBlockingMapper {

    CourtBlockingDto toDto(CourtBlockingEntity courtBlockingEntity);

    CourtBlockingEntity toEntity(CourtBlockingDto courtBlockingDto);

    @Mapping(target = "id", ignore = true)
    void updateEntity (@MappingTarget CourtBlockingEntity target, CourtBlockingDto courtBlockingDto);
}
