package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.SeasonDto;
import cz.reservation.dto.mapper.SeasonMapper;
import cz.reservation.entity.repository.SeasonRepository;
import cz.reservation.service.serviceinterface.SeasonService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
public class SeasonServiceImpl implements SeasonService {

    private final SeasonMapper seasonMapper;

    private final SeasonRepository seasonRepository;

    private static final String SERVICE_NAME = "season";

    public SeasonServiceImpl(SeasonMapper seasonMapper, SeasonRepository seasonRepository) {
        this.seasonMapper = seasonMapper;
        this.seasonRepository = seasonRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<SeasonDto> createSeason(SeasonDto seasonDto) {

        var savedEntity = seasonRepository.save(seasonMapper.toEntity(seasonDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(seasonMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public List<SeasonDto> getAllSeasons() {
        return seasonRepository.findAll().stream().map(seasonMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<SeasonDto> getSeason(Long id) {

        return ResponseEntity
                .ok(seasonMapper.toDto(seasonRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional
    public ResponseEntity<SeasonDto> editSeason(SeasonDto seasonDto, Long id) {
        if (!seasonRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToSave = seasonMapper.toEntity(seasonDto);
            entityToSave.setId(id);
            var savedEntity = seasonRepository.save(entityToSave);
            return ResponseEntity
                    .ok(seasonMapper.toDto(savedEntity));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteSeason(Long id) {
        if (!seasonRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));

        } else {
            seasonRepository.deleteById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }

    }
}
