package cz.reservation.dto.mapper;

import cz.reservation.dto.EnrollmentDto;
import cz.reservation.entity.EnrollmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    EnrollmentDto toDto(EnrollmentEntity entity);

    EnrollmentEntity toEntity(EnrollmentDto enrollmentDto);
}
