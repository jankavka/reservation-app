package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.entity.CourtBlockingEntity;
import cz.reservation.entity.filter.CourtBlockingFilter;

import java.util.List;

public interface CourtBlockingService {

    CourtBlockingDto createBlocking(CourtBlockingDto courtBlockingDto);

    CourtBlockingDto getBlocking(Long id);

    CourtBlockingEntity getBlockingEntity(Long id);

    List<CourtBlockingDto> getAllBlockings(CourtBlockingFilter courtBlockingFilter);

    List<CourtBlockingEntity> getAllBlockingsEntities();

    void editBlocking(CourtBlockingDto courtBlockingDto, Long id);

    void deleteBlocking(Long id);
}
