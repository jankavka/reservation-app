package cz.reservation.service.serviceinterface;

import cz.reservation.dto.SeasonDto;

import java.util.List;

public interface SeasonService {

    SeasonDto createSeason(SeasonDto seasonDto);

    List<SeasonDto> getAllSeasons();

    SeasonDto getSeason(Long id);

    void editSeason(SeasonDto seasonDto, Long id);

    void deleteSeason(Long id);
}
