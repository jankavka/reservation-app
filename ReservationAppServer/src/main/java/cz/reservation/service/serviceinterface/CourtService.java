package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CourtDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CourtService {

    ResponseEntity<CourtDto> createCourt(CourtDto courtDto);

    ResponseEntity<CourtDto> getCourt(Long id);

    ResponseEntity<List<CourtDto>> getAllCourts();

    ResponseEntity<Map<String, String>> deleteCourt(Long id);

    ResponseEntity<Map<String, String>> editCourt(CourtDto courtDto, Long id);
}
