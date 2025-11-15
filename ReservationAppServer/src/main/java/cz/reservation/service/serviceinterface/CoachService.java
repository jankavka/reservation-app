package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CoachDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CoachService {

    ResponseEntity<CoachDto> createCoach(CoachDto coachDto);

    ResponseEntity<CoachDto> getCoach(Long id);

    ResponseEntity<List<CoachDto>> getAllCoaches();

    ResponseEntity<HttpStatus> deleteCoach(Long id);

    ResponseEntity<CoachDto> editCoach(CoachDto coachDto, Long id);
}
