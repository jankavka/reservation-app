package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.entity.CourtBlockingEntity;

import java.util.List;

public interface CourtBlockingService {

    CourtBlockingDto createBlocking(CourtBlockingDto courtBlockingDto);

    CourtBlockingDto getBlocking(Long id);

    CourtBlockingEntity getBlockingEntity(Long id);

    List<CourtBlockingDto> getAllBlockings();

    List<CourtBlockingEntity> getAllBlockingsEntities();

    void editBlocking(CourtBlockingDto courtBlockingDto, Long id);

    void deleteBlocking(Long id);
}
