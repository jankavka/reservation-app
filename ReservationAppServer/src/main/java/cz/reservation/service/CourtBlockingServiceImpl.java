package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.dto.mapper.CourtBlockingMapper;
import cz.reservation.entity.repository.CourtBlockingRepository;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.service.serviceinterface.CourtBlockingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
public class CourtBlockingServiceImpl implements CourtBlockingService {

    private final CourtBlockingMapper courtBlockingMapper;

    private final CourtBlockingRepository courtBlockingRepository;

    private final CourtRepository courtRepository;

    private static final String SERVICE_NAME = "blocking";

    public CourtBlockingServiceImpl(
            CourtBlockingRepository courtBlockingRepository,
            CourtBlockingMapper courtBlockingMapper,
            CourtRepository courtRepository
    ) {
        this.courtBlockingMapper = courtBlockingMapper;
        this.courtBlockingRepository = courtBlockingRepository;
        this.courtRepository = courtRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<CourtBlockingDto> createBlocking(CourtBlockingDto courtBlockingDto) {

        var entityToSave = courtBlockingMapper.toEntity(courtBlockingDto);
        var courtId = courtBlockingDto.court().id();
        if (courtRepository.existsById(courtId)) {
            entityToSave.setCourt(courtRepository.getReferenceById(courtId));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("court", courtId));
        }
        var savedEntity = courtBlockingRepository.save(entityToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(courtBlockingMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CourtBlockingDto> getBlocking(Long id) {

        return ResponseEntity
                .ok(courtBlockingMapper
                        .toDto(courtBlockingRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                        entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CourtBlockingDto>> getAllBlockings() {
        return ResponseEntity
                .ok(courtBlockingRepository
                        .findAll()
                        .stream()
                        .map(courtBlockingMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editBlocking(CourtBlockingDto courtBlockingDto, Long id) {
        if (!courtBlockingRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToUpdate = courtBlockingRepository.getReferenceById(id);
            courtBlockingMapper.updateEntity(entityToUpdate, courtBlockingDto);
            return ResponseEntity.ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED)));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteBlocking(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            courtBlockingRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }
    }
}
