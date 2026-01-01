package cz.reservation.controller;

import cz.reservation.dto.EnrollmentDto;
import cz.reservation.service.serviceinterface.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;


    public EnrollmentController(EnrollmentService enrollmentService){
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentDto> createEnrollment(@RequestBody @Valid EnrollmentDto enrollmentDto){
        return enrollmentService.createEnrollment(enrollmentDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDto> getEnrollment(@PathVariable Long id){
        return enrollmentService.getEnrollment(id);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentDto>> getAllEnrollments(){
        return enrollmentService.getAllEnrollments();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editEnrollment(@RequestBody @Valid EnrollmentDto enrollmentDto, @PathVariable Long id){
        return enrollmentService.editEnrollment(enrollmentDto,id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEnrollment(@PathVariable Long id){
        return enrollmentService.deleteEnrollment(id);
    }
}
