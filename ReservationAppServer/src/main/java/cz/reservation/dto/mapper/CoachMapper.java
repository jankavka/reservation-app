package cz.reservation.dto.mapper;

import cz.reservation.dto.CoachDto;
import cz.reservation.entity.CoachEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoachMapper {

    CoachEntity toEntity(CoachDto coachDto);

    CoachDto toDto(CoachEntity coachEntity);


}
