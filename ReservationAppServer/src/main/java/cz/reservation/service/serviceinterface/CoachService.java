package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CoachDto;
import org.hibernate.NonUniqueObjectException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CoachService {

    ResponseEntity<CoachDto> createCoach(CoachDto coachDto) throws NonUniqueObjectException;

    ResponseEntity<CoachDto> getCoach(Long id);

    ResponseEntity<List<CoachDto>> getAllCoaches();

    ResponseEntity<Map<String,String>> deleteCoach(Long id);

    ResponseEntity<Map<String, String>> editCoach(CoachDto coachDto, Long id);
}
