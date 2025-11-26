package cz.reservation.service;

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

@Service
public class CourtBlockingServiceImpl implements CourtBlockingService {

    private final CourtBlockingMapper courtBlockingMapper;

    private final CourtBlockingRepository courtBlockingRepository;

    private final CourtRepository courtRepository;

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
        if (courtBlockingDto == null) {
            throw new IllegalArgumentException("blocking must not be null");
        } else {
            var entityToSave = courtBlockingMapper.toEntity(courtBlockingDto);
            entityToSave.setCourt(courtRepository.getReferenceById(courtBlockingDto.court().id()));
            var savedEntity = courtBlockingRepository.save(entityToSave);
            return ResponseEntity.status(HttpStatus.CREATED).body(courtBlockingMapper.toDto(savedEntity));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CourtBlockingDto> getBlocking(Long id) {

        return ResponseEntity
                .ok(courtBlockingMapper
                        .toDto(courtBlockingRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "Court blocking with id " + id + " not found"))));

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
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        } else if (!courtBlockingRepository.existsById(id)) {
            throw new EntityNotFoundException("Court blocking with id " + id + " not found");
        } else if (courtBlockingDto == null) {
            throw new IllegalArgumentException("Court blocking must not be null");
        } else {
            var entityToUpdate = courtBlockingRepository.getReferenceById(id);
            courtBlockingMapper.updateEntity(entityToUpdate, courtBlockingDto);
            return ResponseEntity.ok(Map.of("message", "Court blocking updated"));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteBlocking(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        } else if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException("Court blocking with id " + id + " not found");
        } else {
            courtBlockingRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Court blocking with id " + id + " deleted successfully"));
        }
    }
}
