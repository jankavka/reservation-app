package cz.reservation.service.serviceinterface;

import cz.reservation.dto.WeatherNotesDto;

import java.util.List;

public interface WeatherNotesService {

    WeatherNotesDto createWeatherNote(WeatherNotesDto weatherNotesDto);

    WeatherNotesDto getWeatherNote(Long id);

    List<WeatherNotesDto> getAllWeatherNotes();

    void editWeatherNote(WeatherNotesDto weatherNotesDto, Long id);

    void deleteWeatherNote(Long id);
}
