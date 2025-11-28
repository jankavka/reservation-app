package cz.reservation.controller;

import cz.reservation.dto.AttendanceDto;
import cz.reservation.service.serviceinterface.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<AttendanceDto> createAttendance(@RequestBody @Valid AttendanceDto attendanceDto) {
        return attendanceService.createAttendance(attendanceDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDto> getAttendance(@PathVariable Long id) {
        return attendanceService.getAttendance(id);
    }

    @GetMapping
    public ResponseEntity<List<AttendanceDto>> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editAttendance(
            @RequestBody @Valid AttendanceDto attendanceDto, @PathVariable Long id) {
        return attendanceService.editAttendance(attendanceDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAttendance(@PathVariable Long id) {
        return attendanceService.deleteAttendance(id);
    }

}
