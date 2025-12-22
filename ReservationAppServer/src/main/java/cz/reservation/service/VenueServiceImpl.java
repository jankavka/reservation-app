package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.VenueDto;
import cz.reservation.dto.mapper.VenueMapper;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.service.serviceinterface.VenueService;
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
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    private final VenueMapper venueMapper;

    private static final String SERVICE_NAME = "venue";

    @Override
    @Transactional
    public ResponseEntity<VenueDto> createVenue(VenueDto venueDto) {

        var entityToSave = venueMapper.toEntity(venueDto);
        var savedEntity = venueRepository.save(entityToSave);
        return ResponseEntity.ok(venueMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<VenueDto> getVenue(Long id) {

        return ResponseEntity.status(HttpStatus.CREATED).body(venueMapper
                .toDto(venueRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional
    public ResponseEntity<VenueDto> editVenue(VenueDto venueDto, Long id) {
        if (!venueRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));

        } else {
            var entityToSave = venueMapper.toEntity(venueDto);
            entityToSave.setId(id);
            var savedEntity = venueRepository.save(entityToSave);

            return ResponseEntity.ok(venueMapper.toDto(savedEntity));
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<VenueDto>> getAllVenues() {
        return ResponseEntity.ok(venueRepository
                .findAll()
                .stream()
                .map(venueMapper::toDto)
                .toList()
        );
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            venueRepository.deleteById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }
    }
}
