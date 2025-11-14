package cz.reservation.service;

import cz.reservation.dto.SeasonDto;
import cz.reservation.service.serviceinterface.SeasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonServiceImpl implements SeasonService {


    @Override
    public ResponseEntity<SeasonDto> createSeason(SeasonDto seasonDto) {
        return null;
    }

    @Override
    public List<SeasonDto> getAllSeasons() {
        return List.of();
    }

    @Override
    public ResponseEntity<SeasonDto> getSeason(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<SeasonDto> editSeason(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<SeasonDto> deleteSeason(Long id) {
        return null;
    }
}
