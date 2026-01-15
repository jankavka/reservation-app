package cz.reservation.service.serviceinterface;

import cz.reservation.dto.AttendanceDto;

import java.util.List;

public interface AttendanceService {

    AttendanceDto createAttendance(AttendanceDto attendanceDto);

    AttendanceDto getAttendance(Long id);

    List<AttendanceDto> getAllAttendances();

    void editAttendance(AttendanceDto attendanceDto, Long id);

    void deleteAttendance(Long id);
}
