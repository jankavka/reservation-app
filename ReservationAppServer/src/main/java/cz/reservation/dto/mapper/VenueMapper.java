package cz.reservation.dto.mapper;

import cz.reservation.dto.VenueDto;
import cz.reservation.entity.VenueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    VenueEntity toEntity(VenueDto venueDto);

    VenueDto toDto(VenueEntity venueEntity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget VenueEntity target, VenueDto source);
}
