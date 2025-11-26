package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CourtBlockingDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CourtBlockingService {

    ResponseEntity<CourtBlockingDto> createBlocking(CourtBlockingDto courtBlockingDto);

    ResponseEntity<CourtBlockingDto> getBlocking(Long id);

    ResponseEntity<List<CourtBlockingDto>> getAllBlockings();

    ResponseEntity<Map<String, String>> editBlocking(CourtBlockingDto courtBlockingDto, Long id);

    ResponseEntity<Map<String, String>> deleteBlocking(Long id);
}
