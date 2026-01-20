package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.WeatherNotesDto;
import cz.reservation.dto.mapper.WeatherNotesMapper;
import cz.reservation.entity.WeatherNotesEntity;
import cz.reservation.entity.repository.TrainingSlotRepository;
import cz.reservation.entity.repository.WeatherNotesRepository;
import cz.reservation.service.serviceinterface.WeatherNotesService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class WeatherNotesServiceImpl implements WeatherNotesService {

    private final WeatherNotesMapper weatherNotesMapper;

    private final WeatherNotesRepository weatherNotesRepository;

    private final TrainingSlotRepository trainingSlotRepository;

    private static final String SERVICE_NAME = "weather notes";

    @Override
    @Transactional
    public WeatherNotesDto createWeatherNote(WeatherNotesDto weatherNotesDto) {
        var entityToSave = weatherNotesMapper.toEntity(weatherNotesDto);
        setForeignKeys(entityToSave, weatherNotesDto);
        var savedEntity = weatherNotesRepository.save(entityToSave);

        return weatherNotesMapper.toDto(savedEntity);

    }

    @Override
    @Transactional(readOnly = true)
    public WeatherNotesDto getWeatherNote(Long id) {

        return weatherNotesMapper.toDto(weatherNotesRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id))));

    }

    @Override
    @Transactional(readOnly = true)
    public List<WeatherNotesDto> getAllWeatherNotes() {
        return weatherNotesRepository
                .findAll()
                .stream()
                .map(weatherNotesMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void editWeatherNote(WeatherNotesDto weatherNotesDto, Long id) {

        var entityToUpdate = weatherNotesRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));

        weatherNotesMapper.updateEntity(entityToUpdate, weatherNotesDto);
        setForeignKeys(entityToUpdate, weatherNotesDto);

    }

    @Override
    @Transactional
    public void deleteWeatherNote(Long id) {
        if (!weatherNotesRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            weatherNotesRepository.deleteById(id);
        }
    }

    private void setForeignKeys(WeatherNotesEntity target, WeatherNotesDto source) {
        target.setTrainingSlot(trainingSlotRepository
                .findById(source.trainingSlot().id())
                .orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                                "training slot", source.trainingSlot().id()))));
    }
}
