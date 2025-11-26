package cz.reservation.service.serviceinterface;

import cz.reservation.dto.WeatherNotesDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface WeatherNotesService {

    ResponseEntity<WeatherNotesDto> createWeatherNote(WeatherNotesDto weatherNotesDto);

    ResponseEntity<WeatherNotesDto> getWeatherNote(Long id);

    ResponseEntity<List<WeatherNotesDto>> getAllWeatherNotes();

    ResponseEntity<Map<String, String>> editWeatherNote(WeatherNotesDto weatherNotesDto, Long id);

    ResponseEntity<Map<String, String>> deleteWeatherNote(Long id);
}
