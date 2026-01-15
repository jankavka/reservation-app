package cz.reservation.controller;

import cz.reservation.dto.SeasonDto;
import cz.reservation.service.serviceinterface.SeasonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(seasonService.createSeason(seasonDto));
    }

    @GetMapping("/{id}")
    public SeasonDto getSeason(@PathVariable Long id) {
        return seasonService.getSeason(id);
    }

    @GetMapping
    public List<SeasonDto> getAllSeasons() {
        return seasonService.getAllSeasons();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editSeason(
            @RequestBody @Valid SeasonDto seasonDto,
            @PathVariable Long id) {
        seasonService.editSeason(seasonDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeason(@PathVariable Long id) {
        seasonService.deleteSeason(id);
    }
}
