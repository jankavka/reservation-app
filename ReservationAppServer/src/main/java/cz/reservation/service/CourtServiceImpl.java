package cz.reservation.service;

import cz.reservation.dto.CourtDto;
import cz.reservation.dto.mapper.CourtMapper;
import cz.reservation.entity.CourtEntity;
import cz.reservation.entity.filter.CourtFilter;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.entity.repository.specification.CourtSpecification;
import cz.reservation.service.serviceinterface.CourtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

@Service
@RequiredArgsConstructor
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;
    private final CourtMapper courtMapper;
    private final VenueRepository venueRepository;

    private static final String SERVICE_NAME = "court";

    @Override
    @Transactional
    public CourtDto createCourt(CourtDto courtDto) {
        var entityToSave = courtMapper.toEntity(courtDto);
        setForeignKeys(entityToSave, courtDto);
        var savedEntity = courtRepository.save(entityToSave);
        return courtMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CourtDto getCourt(Long id) {
        return courtMapper.toDto(courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtDto> getAllCourts(CourtFilter courtFilter) {
        var spec = new CourtSpecification(courtFilter);
        return courtRepository.findAll(spec).stream()
                .map(courtMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteCourt(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        courtRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void editCourt(CourtDto courtDto, Long id) {
        var entityToUpdate = courtRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        courtMapper.updateEntity(entityToUpdate, courtDto);
        setForeignKeys(entityToUpdate, courtDto);
    }

    private void setForeignKeys(CourtEntity target, CourtDto source) {
        var venueId = source.venue().id();
        target.setVenue(venueRepository
                .findById(venueId)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage("venue", venueId))));
    }
}
