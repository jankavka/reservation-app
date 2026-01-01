package cz.reservation.dto.mapper;

import cz.reservation.dto.CoachDto;
import cz.reservation.entity.CoachEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface CoachMapper {

    CoachEntity toEntity(CoachDto coachDto);

    CoachDto toDto(CoachEntity coachEntity);

    void updateEntity(@MappingTarget CoachEntity target, CoachDto source);


}
