package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.CourtDto;
import cz.reservation.dto.mapper.CourtMapper;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.service.serviceinterface.CourtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;

    private final CourtMapper courtMapper;

    private final VenueRepository venueRepository;

    private static final String SERVICE_NAME = "court";

    @Override
    @Transactional
    public ResponseEntity<CourtDto> createCourt(CourtDto courtDto) {

        var entityToSave = courtMapper.toEntity(courtDto);
        var venueId = courtDto.venue().id();
        if (venueRepository.existsById(venueId)) {
            entityToSave.setVenue(venueRepository.getReferenceById(venueId));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("venue", venueId));
        }
        var savedEntity = courtRepository.save(entityToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(courtMapper.toDto(savedEntity));


    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CourtDto> getCourt(Long id) throws EntityNotFoundException {

        return ResponseEntity.ok(courtMapper.toDto(courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CourtDto>> getAllCourts() {
        return ResponseEntity.ok(courtRepository.findAll().stream()
                .map(courtMapper::toDto)
                .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteCourt(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            courtRepository.deleteById(id);

            var responseMessage = new HashMap<String, String>();
            responseMessage.put("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED));

            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        }


    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editCourt(CourtDto courtDto, Long id) {
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToUpdate = courtRepository.getReferenceById(id);
            courtMapper.updateEntity(entityToUpdate, courtDto);
            var updatedDto = courtMapper.toDto(courtRepository.getReferenceById(id));
            return ResponseEntity.ok(Map.of(
                    "message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED),
                    "object", updatedDto.toString()));
        }


    }
}
