package cz.reservation.controller;

import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.service.serviceinterface.CourtBlockingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/court-block")
public class CourtBlockingController {

    private final CourtBlockingService courtBlockingService;

    public CourtBlockingController(CourtBlockingService courtBlockingService) {
        this.courtBlockingService = courtBlockingService;
    }


    @PostMapping
    public ResponseEntity<CourtBlockingDto> createBlocking(@Valid @RequestBody CourtBlockingDto courtBlockingDto) {
        return courtBlockingService.createBlocking(courtBlockingDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtBlockingDto> getBlocking(@PathVariable Long id) {
        return courtBlockingService.getBlocking(id);
    }

    @GetMapping
    public ResponseEntity<List<CourtBlockingDto>> getAllBlockings() {
        return courtBlockingService.getAllBlockings();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editBlocking(
            @Valid @RequestBody CourtBlockingDto courtBlockingDto, @PathVariable Long id) {

        return courtBlockingService.editBlocking(courtBlockingDto,id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBlocking(@PathVariable Long id){
        return courtBlockingService.deleteBlocking(id);
    }
}
