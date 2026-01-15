package cz.reservation.controller;

import cz.reservation.dto.EnrollmentDto;
import cz.reservation.service.serviceinterface.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;


    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentDto createEnrollment(@RequestBody @Valid EnrollmentDto enrollmentDto) {
        return enrollmentService.createEnrollment(enrollmentDto);
    }

    @GetMapping("/{id}")
    public EnrollmentDto getEnrollment(@PathVariable Long id) {
        return enrollmentService.getEnrollment(id);
    }

    @GetMapping
    public List<EnrollmentDto> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editEnrollment(@RequestBody @Valid EnrollmentDto enrollmentDto, @PathVariable Long id) {
        enrollmentService.editEnrollment(enrollmentDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
    }
}
