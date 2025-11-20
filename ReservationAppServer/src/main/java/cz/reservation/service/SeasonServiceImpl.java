package cz.reservation.service;

import cz.reservation.dto.SeasonDto;
import cz.reservation.dto.mapper.SeasonMapper;
import cz.reservation.entity.SeasonEntity;
import cz.reservation.entity.repository.SeasonRepository;
import cz.reservation.service.serviceinterface.SeasonService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SeasonServiceImpl implements SeasonService {

    private final SeasonMapper seasonMapper;

    private final SeasonRepository seasonRepository;

    @Autowired
    public SeasonServiceImpl(SeasonMapper seasonMapper, SeasonRepository seasonRepository) {
        this.seasonMapper = seasonMapper;
        this.seasonRepository = seasonRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<SeasonDto> createSeason(SeasonDto seasonDto) {
        if (seasonDto == null) {
            throw new IllegalArgumentException("Season must not be null");

        } else {
            SeasonEntity savedEntity = seasonRepository.save(seasonMapper.toEntity(seasonDto));
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(seasonMapper.toDto(savedEntity));
        }
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
                        .orElseThrow(EntityNotFoundException::new)));
    }

    @Override
    @Transactional
    public ResponseEntity<SeasonDto> editSeason(SeasonDto seasonDto, Long id) {
        if (seasonRepository.existsById(id)) {
            SeasonEntity entityToSave = seasonMapper.toEntity(seasonDto);
            entityToSave.setId(id);
            SeasonEntity savedEntity = seasonRepository.save(entityToSave);
            return ResponseEntity
                    .ok(seasonMapper.toDto(savedEntity));
        } else {
            throw new EntityNotFoundException("Season to edit not found");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteSeason(Long id) {
        if (seasonRepository.existsById(id)) {
            seasonRepository.deleteById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "Season with id " + " was deleted"));
        } else {
            throw new EntityNotFoundException("Season to delete not found");
        }

    }
}
