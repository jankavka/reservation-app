package cz.reservation.service;

import cz.reservation.dto.CourtDto;
import cz.reservation.dto.mapper.CourtMapper;
import cz.reservation.entity.CourtEntity;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.service.serviceinterface.CourtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;

    private final CourtMapper courtMapper;

    @Autowired
    public CourtServiceImpl(CourtRepository courtRepository, CourtMapper courtMapper) {
        this.courtMapper = courtMapper;
        this.courtRepository = courtRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<HttpStatus> createCourt(CourtDto courtDto) throws NullPointerException {
        if (courtDto != null) {
            courtRepository.save(courtMapper.toEntity(courtDto));
            return ResponseEntity.ok(HttpStatus.CREATED);

        } else {
            throw new NullPointerException("Internal server error. Court must not be null");

        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CourtDto> getCourt(Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(courtMapper.toDto(courtRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new)));
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
    public ResponseEntity<HttpStatus> deleteCourt(Long id) throws EntityNotFoundException, IllegalArgumentException {
        if (id != null) {
            if (courtRepository.existsById(id)) {
                courtRepository.deleteById(id);
            } else {
                throw new EntityNotFoundException("Entity with id " + id + " not found");
            }

            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Id must not be null");
        }


    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> editCourt(CourtDto courtDto, Long id) {
        if (courtRepository.existsById(id) && courtDto != null) {
            CourtEntity editedEntityToSave = courtMapper.toEntity(courtDto);
            editedEntityToSave.setId(id);
            courtRepository.save(editedEntityToSave);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("Entity with id " + id + "not found");
        }


    }
}
