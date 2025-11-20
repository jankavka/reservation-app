package cz.reservation.service.serviceinterface;

import cz.reservation.dto.SeasonDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface SeasonService {

    ResponseEntity<SeasonDto> createSeason(SeasonDto seasonDto);

    List<SeasonDto> getAllSeasons();

    ResponseEntity<SeasonDto> getSeason(Long id);

    ResponseEntity<SeasonDto> editSeason(SeasonDto seasonDto, Long id);

    ResponseEntity<Map<String, String>> deleteSeason(Long id);
}
