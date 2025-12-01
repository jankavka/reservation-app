package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.entity.CourtBlockingEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CourtBlockingService {

    ResponseEntity<CourtBlockingDto> createBlocking(CourtBlockingDto courtBlockingDto);

    ResponseEntity<CourtBlockingDto> getBlocking(Long id);

    CourtBlockingEntity getBlockingEntity(Long id);

    ResponseEntity<List<CourtBlockingDto>> getAllBlockings();

    List<CourtBlockingEntity> getAllBlockingsEntities();

    ResponseEntity<Map<String, String>> editBlocking(CourtBlockingDto courtBlockingDto, Long id);

    ResponseEntity<Map<String, String>> deleteBlocking(Long id);
}
