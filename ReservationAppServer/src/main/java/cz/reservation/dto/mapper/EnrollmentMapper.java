package cz.reservation.dto.mapper;

import cz.reservation.dto.EnrollmentDto;
import cz.reservation.entity.EnrollmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    EnrollmentDto toDto(EnrollmentEntity entity);

    EnrollmentEntity toEntity(EnrollmentDto enrollmentDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "player", ignore = true)
    @Mapping(target = "group", ignore = true)
    void updateEntity(@MappingTarget EnrollmentEntity target, EnrollmentDto source);
}
