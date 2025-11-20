package cz.reservation.dto.mapper;

import cz.reservation.dto.VenueDto;
import cz.reservation.entity.VenueEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    VenueEntity toEntity(VenueDto venueDto);

    VenueDto toDto(VenueEntity venueEntity);
}
