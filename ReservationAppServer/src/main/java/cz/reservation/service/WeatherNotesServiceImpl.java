package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.WeatherNotesDto;
import cz.reservation.dto.mapper.WeatherNotesMapper;
import cz.reservation.entity.repository.TrainingSlotRepository;
import cz.reservation.entity.repository.WeatherNotesRepository;
import cz.reservation.service.serviceinterface.WeatherNotesService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
public class WeatherNotesServiceImpl implements WeatherNotesService {

    private final WeatherNotesMapper weatherNotesMapper;

    private final WeatherNotesRepository weatherNotesRepository;

    private final TrainingSlotRepository trainingSlotRepository;

    private static final String SERVICE_NAME = "weather notes";


    public WeatherNotesServiceImpl(
            WeatherNotesRepository weatherNotesRepository,
            WeatherNotesMapper weatherNotesMapper,
            TrainingSlotRepository trainingSlotRepository
    ) {
        this.weatherNotesMapper = weatherNotesMapper;
        this.weatherNotesRepository = weatherNotesRepository;
        this.trainingSlotRepository = trainingSlotRepository;

    }


    @Override
    @Transactional
    public ResponseEntity<WeatherNotesDto> createWeatherNote(WeatherNotesDto weatherNotesDto) {
        var entityToSave = weatherNotesMapper.toEntity(weatherNotesDto);
        entityToSave.setTrainingSlot(trainingSlotRepository.getReferenceById(weatherNotesDto.trainingSlot().id()));
        var savedEntity = weatherNotesRepository.save(entityToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(weatherNotesMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<WeatherNotesDto> getWeatherNote(Long id) {

        return ResponseEntity
                .ok(weatherNotesMapper.toDto(weatherNotesRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<WeatherNotesDto>> getAllWeatherNotes() {
        return ResponseEntity.ok(weatherNotesRepository
                .findAll()
                .stream()
                .map(weatherNotesMapper::toDto)
                .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editWeatherNote(WeatherNotesDto weatherNotesDto, Long id) {

        if (weatherNotesRepository.existsById(id)) {
            var entityToUpdate = weatherNotesRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                            entityNotFoundExceptionMessage(SERVICE_NAME, id)));

            weatherNotesMapper.updateEntity(entityToUpdate, weatherNotesDto);

            return ResponseEntity.ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED)));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }

    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteWeatherNote(Long id) {
        if (!weatherNotesRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            weatherNotesRepository.deleteById(id);

            return ResponseEntity.ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }
    }
}
