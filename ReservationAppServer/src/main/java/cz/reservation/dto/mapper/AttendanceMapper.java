package cz.reservation.dto.mapper;

import cz.reservation.dto.AttendanceDto;
import cz.reservation.entity.AttendanceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = BookingMapper.class)
public interface AttendanceMapper {

    AttendanceEntity toEntity(AttendanceDto attendanceDto);

    AttendanceDto toDto(AttendanceEntity attendanceEntity);

    @Mapping(target = "booking", ignore = true)
    void updateEntity(@MappingTarget AttendanceEntity target, AttendanceDto source);
}
