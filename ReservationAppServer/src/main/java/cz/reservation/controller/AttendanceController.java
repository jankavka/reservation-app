package cz.reservation.controller;

import cz.reservation.dto.AttendanceDto;
import cz.reservation.entity.filter.AttendanceFilter;
import cz.reservation.service.serviceinterface.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceDto createAttendance(@RequestBody @Valid AttendanceDto attendanceDto) {
        return attendanceService.createAttendance(attendanceDto);
    }

    @GetMapping("/{id}")
    public AttendanceDto getAttendance(@PathVariable Long id) {
        return attendanceService.getAttendance(id);
    }

    @GetMapping
    public List<AttendanceDto> getAllAttendances(AttendanceFilter attendanceFilter) {
        return attendanceService.getAllAttendances(attendanceFilter);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editAttendance(@RequestBody @Valid AttendanceDto attendanceDto, @PathVariable Long id) {
        attendanceService.editAttendance(attendanceDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
    }

}
