package cz.reservation.service.serviceinterface;

import cz.reservation.dto.EnrollmentDto;
import cz.reservation.entity.filter.EnrollmentFilter;

import java.util.List;

public interface EnrollmentService {

    EnrollmentDto createEnrollment(EnrollmentDto enrollmentDto);

    EnrollmentDto getEnrollment(Long id);

    List<EnrollmentDto> getAllEnrollments(EnrollmentFilter enrollmentFilter);

    void editEnrollment(EnrollmentDto enrollmentDto, Long id);

    void deleteEnrollment(Long id);
}
