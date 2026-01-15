package cz.reservation.service.serviceinterface;

import cz.reservation.dto.CoachDto;

import java.util.List;

public interface CoachService {

    CoachDto createCoach(CoachDto coachDto);

    CoachDto getCoach(Long id);

    List<CoachDto> getAllCoaches();

    void deleteCoach(Long id);

    void editCoach(CoachDto coachDto, Long id);
}
