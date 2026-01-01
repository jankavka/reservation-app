package cz.reservation.service.serviceinterface;

import cz.reservation.dto.EnrollmentDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface EnrollmentService {

    ResponseEntity<EnrollmentDto> createEnrollment(EnrollmentDto enrollmentDto);

    ResponseEntity<EnrollmentDto> getEnrollment(Long id);

    ResponseEntity<List<EnrollmentDto>> getAllEnrollments();

    ResponseEntity<Map<String, String>> editEnrollment(EnrollmentDto enrollmentDto, Long id);

    ResponseEntity<Map<String, String>> deleteEnrollment(Long id);
}
