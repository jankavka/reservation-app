package cz.reservation.dto.mapper;

import cz.reservation.dto.AttendanceDto;
import cz.reservation.entity.AttendanceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    AttendanceEntity toEntity(AttendanceDto attendanceDto);

    AttendanceDto toDto(AttendanceEntity attendanceEntity);
}
