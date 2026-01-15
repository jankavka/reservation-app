package cz.reservation.controller;

import cz.reservation.dto.CoachDto;
import cz.reservation.service.serviceinterface.CoachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    @PostMapping
    public ResponseEntity<CoachDto> createCoach(@RequestBody @Valid CoachDto coachDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coachService.createCoach(coachDto));
    }

    @GetMapping("/{id}")
    public CoachDto getCoach(@PathVariable Long id) {
        return coachService.getCoach(id);
    }

    @GetMapping
    public List<CoachDto> getAllCoaches() {
        return coachService.getAllCoaches();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editCoach(@RequestBody @Valid CoachDto coachDto, @PathVariable Long id) {
        coachService.editCoach(coachDto, id);
    }
}
