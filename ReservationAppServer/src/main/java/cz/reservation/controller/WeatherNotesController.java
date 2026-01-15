package cz.reservation.controller;

import cz.reservation.dto.WeatherNotesDto;
import cz.reservation.service.serviceinterface.WeatherNotesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(weatherNotesService.createWeatherNote(weatherNotesDto));
    }

    @GetMapping("/{id}")
    public WeatherNotesDto getWeatherNote(@PathVariable Long id) {
        return weatherNotesService.getWeatherNote(id);
    }

    @GetMapping
    public List<WeatherNotesDto> getAllWeatherNotes() {
        return weatherNotesService.getAllWeatherNotes();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editWeatherNote(
            @RequestBody @Valid WeatherNotesDto weatherNotesDto,
            @PathVariable Long id) {

        weatherNotesService.editWeatherNote(weatherNotesDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeatherNote(@PathVariable Long id) {
        weatherNotesService.deleteWeatherNote(id);
    }

}
