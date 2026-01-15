package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.SeasonDto;
import cz.reservation.dto.mapper.SeasonMapper;
import cz.reservation.entity.repository.SeasonRepository;
import cz.reservation.service.serviceinterface.SeasonService;
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
public class SeasonServiceImpl implements SeasonService {

    private final SeasonMapper seasonMapper;

    private final SeasonRepository seasonRepository;

    private static final String SERVICE_NAME = "season";

    @Override
    @Transactional
    public SeasonDto createSeason(SeasonDto seasonDto) {

        var savedEntity = seasonRepository.save(seasonMapper.toEntity(seasonDto));
        return seasonMapper.toDto(savedEntity);

    }

    @Override
    @Transactional(readOnly = true)
    public List<SeasonDto> getAllSeasons() {
        return seasonRepository.findAll().stream().map(seasonMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SeasonDto getSeason(Long id) {

        return seasonMapper.toDto(seasonRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id))));

    }

    @Override
    @Transactional
    public void editSeason(SeasonDto seasonDto, Long id) {
        var entityToUpdate = seasonRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));

        seasonMapper.updateEntity(entityToUpdate, seasonDto);
    }

    @Override
    @Transactional
    public void deleteSeason(Long id) {
        if (!seasonRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            seasonRepository.deleteById(id);
        }

    }
}
