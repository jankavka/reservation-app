package cz.reservation.controller;

import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.service.serviceinterface.CourtBlockingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/court-block")
public class CourtBlockingController {

    private final CourtBlockingService courtBlockingService;

    public CourtBlockingController(CourtBlockingService courtBlockingService) {
        this.courtBlockingService = courtBlockingService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourtBlockingDto createBlocking(@Valid @RequestBody CourtBlockingDto courtBlockingDto) {
        return courtBlockingService.createBlocking(courtBlockingDto);
    }

    @GetMapping("/{id}")
    public CourtBlockingDto getBlocking(@PathVariable Long id) {
        return courtBlockingService.getBlocking(id);
    }

    @GetMapping
    public List<CourtBlockingDto> getAllBlockings() {
        return courtBlockingService.getAllBlockings();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editBlocking(@Valid @RequestBody CourtBlockingDto courtBlockingDto, @PathVariable Long id) {
        courtBlockingService.editBlocking(courtBlockingDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlocking(@PathVariable Long id) {
        courtBlockingService.deleteBlocking(id);
    }
}
