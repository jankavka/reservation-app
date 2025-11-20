package cz.reservation.service;

import cz.reservation.dto.VenueDto;
import cz.reservation.dto.mapper.VenueMapper;
import cz.reservation.entity.VenueEntity;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.service.serviceinterface.VenueService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    private final VenueMapper venueMapper;

    @Autowired
    public VenueServiceImpl(VenueRepository venueRepository, VenueMapper venueMapper) {
        this.venueMapper = venueMapper;
        this.venueRepository = venueRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<VenueDto> createVenue(VenueDto venueDto) {
        if (venueDto == null) {
            throw new IllegalArgumentException("Venue must not be null");
        } else {

            VenueEntity entityToSave = venueMapper.toEntity(venueDto);
            VenueEntity savedEntity = venueRepository.save(entityToSave);
            return ResponseEntity.ok(venueMapper.toDto(savedEntity));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<VenueDto> getVenue(Long id) {
        return ResponseEntity.ok(venueMapper
                .toDto(venueRepository
                        .findById(id)
                        .orElseThrow(EntityNotFoundException::new)));
    }

    @Override
    @Transactional
    public ResponseEntity<VenueDto> editVenue(VenueDto venueDto, Long id) {
        if (venueDto == null) {
            throw new IllegalArgumentException("Venue must not be null");

        } else if (!venueRepository.existsById(id)) {
            throw new EntityNotFoundException("Venue not found");

        } else {
            VenueEntity entityToSave = venueMapper.toEntity(venueDto);
            entityToSave.setId(id);
            VenueEntity savedEntity = venueRepository.save(entityToSave);

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
            throw new EntityNotFoundException("Venue not found");
        } else {
            venueRepository.deleteById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "Venue with id " + id + " not found"));
        }
    }
}
