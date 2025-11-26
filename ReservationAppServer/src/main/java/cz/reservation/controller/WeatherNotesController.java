package cz.reservation.controller;

import cz.reservation.dto.WeatherNotesDto;
import cz.reservation.service.serviceinterface.WeatherNotesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather-notes")
public class WeatherNotesController {

    private final WeatherNotesService weatherNotesService;

    public WeatherNotesController(WeatherNotesService weatherNotesService) {
        this.weatherNotesService = weatherNotesService;
    }

    @PostMapping
    public ResponseEntity<WeatherNotesDto> createWeatherNote(@RequestBody @Valid WeatherNotesDto weatherNotesDto) {
        return weatherNotesService.createWeatherNote(weatherNotesDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeatherNotesDto> getWeatherNote(@PathVariable Long id) {
        return weatherNotesService.getWeatherNote(id);
    }

    @GetMapping
    public ResponseEntity<List<WeatherNotesDto>> getAllWeatherNotes() {
        return weatherNotesService.getAllWeatherNotes();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editWeatherNote(
            @RequestBody @Valid WeatherNotesDto weatherNotesDto,
            @PathVariable Long id) {

        return weatherNotesService.editWeatherNote(weatherNotesDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteWeatherNote(@PathVariable Long id) {
        return weatherNotesService.deleteWeatherNote(id);
    }

}
