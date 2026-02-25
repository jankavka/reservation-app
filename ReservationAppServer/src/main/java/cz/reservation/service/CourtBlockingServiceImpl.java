package cz.reservation.service;


import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.dto.mapper.CourtBlockingMapper;
import cz.reservation.entity.CourtBlockingEntity;
import cz.reservation.entity.filter.CourtBlockingFilter;
import cz.reservation.entity.repository.CourtBlockingRepository;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.specification.CourtBlockingSpecification;
import cz.reservation.service.exception.UnsupportedTimeRangeException;
import cz.reservation.service.serviceinterface.CourtBlockingService;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    public CourtBlockingDto createBlocking(CourtBlockingDto courtBlockingDto) {
        timeRangeValidation(courtBlockingDto);
        var savedEntity = createAndSaveBlocking(courtBlockingDto);
        return courtBlockingMapper.toDto(savedEntity);

    }

    @Override
    @Transactional(readOnly = true)
    public CourtBlockingDto getBlocking(Long id) {
        return courtBlockingMapper
                .toDto(courtBlockingRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id))));

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
    public List<CourtBlockingDto> getAllBlockings(CourtBlockingFilter courtBlockingFilter) {
        var spec = new CourtBlockingSpecification(courtBlockingFilter);

        return courtBlockingRepository
                .findAll(spec)
                .stream()
                .map(courtBlockingMapper::toDto)
                .toList();
    }

    @Override
    public List<CourtBlockingEntity> getAllBlockingsEntities() {
        return courtBlockingRepository.findAll();
    }

    @Override
    @Transactional
    public void editBlocking(CourtBlockingDto courtBlockingDto, Long id) {
        var entityToUpdate = courtBlockingRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));

        courtBlockingMapper.updateEntity(entityToUpdate, courtBlockingDto);


    }

    @Override
    @Transactional
    public void deleteBlocking(Long id) {
        if (!courtBlockingRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            courtBlockingRepository.deleteById(id);
        }
    }

    private CourtBlockingEntity createAndSaveBlocking(CourtBlockingDto courtBlockingDto) {
        var entityToSave = courtBlockingMapper.toEntity(courtBlockingDto);
        var courtId = courtBlockingDto.court().id();
        entityToSave.setRange(makeRange(courtBlockingDto.blockedFrom(), courtBlockingDto.blockedTo()));


        //Check for existing court
        entityToSave.setCourt(courtRepository.findById(courtId).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage("court", courtId))));
        return courtBlockingRepository.save(entityToSave);

    }

    private Range<LocalDateTime> makeRange(LocalDateTime from, LocalDateTime to) {
        return Range.
                localDateTimeRange("(" + from +
                        "," + to + ")");
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


    }

}
