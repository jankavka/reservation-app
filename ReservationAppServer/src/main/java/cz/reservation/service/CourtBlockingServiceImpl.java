package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.dto.mapper.CourtBlockingMapper;
import cz.reservation.entity.CourtBlockingEntity;
import cz.reservation.entity.repository.CourtBlockingRepository;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.service.exception.UnsupportedTimeRangeException;
import cz.reservation.service.serviceinterface.CourtBlockingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class CourtBlockingServiceImpl implements CourtBlockingService {

    private final CourtBlockingMapper courtBlockingMapper;

    private final CourtBlockingRepository courtBlockingRepository;

    private final CourtRepository courtRepository;

    private static final String SERVICE_NAME = "blocking";

    @Override
    @Transactional
    public ResponseEntity<CourtBlockingDto> createBlocking(CourtBlockingDto courtBlockingDto) {
        timeRangeValidation(courtBlockingDto);
        var savedEntity = createAndSaveBlocking(courtBlockingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(courtBlockingMapper.toDto(savedEntity));


    }

    @Override
    public CourtBlockingDto createBlockingAndReturnDto(CourtBlockingDto courtBlockingDto) {
        timeRangeValidation(courtBlockingDto);
        var savedEntity = createAndSaveBlocking(courtBlockingDto);
        return courtBlockingMapper.toDto(savedEntity);

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
    public CourtBlockingEntity getBlockingEntity(Long id) {
        if (courtBlockingRepository.existsById(id)) {
            return courtBlockingRepository.getReferenceById(id);
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
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
    public List<CourtBlockingEntity> getAllBlockingsEntities() {
        return courtBlockingRepository.findAll();
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

    private CourtBlockingEntity createAndSaveBlocking(CourtBlockingDto courtBlockingDto) {
        var entityToSave = courtBlockingMapper.toEntity(courtBlockingDto);
        var courtId = courtBlockingDto.court().id();

        //Check for existing court
        entityToSave.setCourt(courtRepository.findById(courtId).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage("court", courtId))));
        return courtBlockingRepository.save(entityToSave);

    }

    /**
     * This helper methods checks if the time for court blocking starts and ends at time with 00 or 30 minutes. Other
     * values are not supported. Also checks if date "from" is before date "to".
     *
     * @param courtBlockingDto Dto with court blocking information
     */
    private void timeRangeValidation(CourtBlockingDto courtBlockingDto) {

        if (!((courtBlockingDto.blockedFrom().getMinute() == 0 ||
                courtBlockingDto.blockedFrom().getMinute() == 30)
                &&
                (courtBlockingDto.blockedTo().getMinute() == 0 ||
                        courtBlockingDto.blockedTo().getMinute() == 30))) {

            throw new UnsupportedTimeRangeException("Unsupported time range. Minutes can be only 30 or 00");
        }
        if (!courtBlockingDto.blockedFrom().isBefore(courtBlockingDto.blockedTo())) {
            throw new IllegalArgumentException("Date `from` has to be before date `to`");

        }


    }

}
