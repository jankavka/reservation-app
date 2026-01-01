package cz.reservation.controller;

import cz.reservation.dto.SeasonDto;
import cz.reservation.service.serviceinterface.SeasonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/season")
public class SeasonController {

    private final SeasonService seasonService;

    @Autowired
    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @PostMapping
    public ResponseEntity<SeasonDto> createSeason(@RequestBody @Valid SeasonDto seasonDto) {
        return seasonService.createSeason(seasonDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeasonDto> getSeason(@PathVariable Long id) {
        return seasonService.getSeason(id);
    }

    @GetMapping
    public List<SeasonDto> getAllSeasons() {
        return seasonService.getAllSeasons();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editSeason(
            @RequestBody @Valid SeasonDto seasonDto,
            @PathVariable Long id) {
        return seasonService.editSeason(seasonDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSeason(@PathVariable Long id) {
        return seasonService.deleteSeason(id);
    }
}
