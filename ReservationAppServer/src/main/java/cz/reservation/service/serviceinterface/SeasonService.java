package cz.reservation.service.serviceinterface;

import cz.reservation.dto.SeasonDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SeasonService {

    ResponseEntity<SeasonDto> createSeason(SeasonDto seasonDto);

    List<SeasonDto> getAllSeasons();

    ResponseEntity<SeasonDto> getSeason(Long id);

    ResponseEntity<SeasonDto> editSeason(SeasonDto seasonDto, Long id);

    ResponseEntity<HttpStatus> deleteSeason(Long id);
}
