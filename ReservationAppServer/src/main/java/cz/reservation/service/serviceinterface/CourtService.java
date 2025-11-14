package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CourtDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CourtService {

    ResponseEntity<HttpStatus> createCourt(CourtDto courtDto);

    ResponseEntity<CourtDto> getCourt(Long id) throws EntityNotFoundException;

    ResponseEntity<List<CourtDto>> getAllCourts();

    ResponseEntity<HttpStatus> deleteCourt(Long id) throws EntityNotFoundException, IllegalArgumentException;

    ResponseEntity<HttpStatus> editCourt(CourtDto courtDto, Long id);
}
