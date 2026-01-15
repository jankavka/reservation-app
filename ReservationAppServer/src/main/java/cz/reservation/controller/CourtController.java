package cz.reservation.controller;

import cz.reservation.dto.CourtDto;
import cz.reservation.service.serviceinterface.CourtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/court")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourtDto createCourt(@RequestBody @Valid CourtDto courtDto) {
        return courtService.createCourt(courtDto);
    }

    @GetMapping("/{id}")
    public CourtDto showCourt(@PathVariable Long id) {
        return courtService.getCourt(id);
    }

    @GetMapping
    public List<CourtDto> showAllCourts() {
        return courtService.getAllCourts();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editCourt(@Valid @RequestBody CourtDto courtDto, @PathVariable Long id) {
        courtService.editCourt(courtDto, id);
    }
}
