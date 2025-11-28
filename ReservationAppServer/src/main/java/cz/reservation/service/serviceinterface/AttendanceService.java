package cz.reservation.service.serviceinterface;

import cz.reservation.dto.AttendanceDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface AttendanceService {

    ResponseEntity<AttendanceDto> createAttendance(AttendanceDto attendanceDto);

    ResponseEntity<AttendanceDto> getAttendance(Long id);

    ResponseEntity<List<AttendanceDto>> getAllAttendances();

    ResponseEntity<Map<String, String>> editAttendance(AttendanceDto attendanceDto, Long id);

    ResponseEntity<Map<String, String>> deleteAttendance(Long id);


}
