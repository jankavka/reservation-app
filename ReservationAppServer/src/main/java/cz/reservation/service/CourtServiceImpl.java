package cz.reservation.service;

import cz.reservation.dto.CourtDto;
import cz.reservation.dto.mapper.CourtMapper;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.service.serviceinterface.CourtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;

    private final CourtMapper courtMapper;

    private final VenueRepository venueRepository;

    public CourtServiceImpl(
            CourtRepository courtRepository,
            CourtMapper courtMapper,
            VenueRepository venueRepository
    ) {
        this.courtMapper = courtMapper;
        this.courtRepository = courtRepository;
        this.venueRepository = venueRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<CourtDto> createCourt(CourtDto courtDto) throws NullPointerException {
        if (courtDto == null) {
            throw new NullPointerException("Court must not be null");

        } else {
            var entityToSave = courtMapper.toEntity(courtDto);
            entityToSave.setVenue(venueRepository.getReferenceById(courtDto.venue().id()));
            var savedEntity = courtRepository.save(entityToSave);
            return ResponseEntity.ok(courtMapper.toDto(savedEntity));

        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CourtDto> getCourt(Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(courtMapper.toDto(courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court not found"))));
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
    public ResponseEntity<Map<String, String>> deleteCourt(Long id) throws EntityNotFoundException, IllegalArgumentException {
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException("Court with id " + id + " not found");
        } else {
            courtRepository.deleteById(id);

            var responseMessage = new HashMap<String, String>();
            responseMessage.put("message", "Court with id " + id + " was deleted");

            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        }


    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> editCourt(CourtDto courtDto, Long id) {
        if (courtDto == null) {
            throw new IllegalArgumentException("Court must not be null");
        } else if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException("Entity with id " + id + "not found");
        } else {
            var editedEntityToSave = courtMapper.toEntity(courtDto);
            editedEntityToSave.setId(id);
            editedEntityToSave.setVenue(venueRepository.getReferenceById(courtDto.venue().id()));
            courtRepository.save(editedEntityToSave);
            return ResponseEntity.ok(HttpStatus.OK);
        }


    }
}
