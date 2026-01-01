package cz.reservation.controller;

import cz.reservation.dto.VenueDto;
import cz.reservation.service.serviceinterface.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/venue")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    public ResponseEntity<VenueDto> createVenue(@RequestBody @Valid VenueDto venueDto) {
        return venueService.createVenue(venueDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueDto> getVenue(@PathVariable Long id) {
        return venueService.getVenue(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editVenue(
            @RequestBody @Valid VenueDto venueDto, @PathVariable Long id) {
        return venueService.editVenue(venueDto, id);
    }

    @GetMapping
    public ResponseEntity<List<VenueDto>> getAllVenues() {
        return venueService.getAllVenues();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVenue(@PathVariable Long id) {
        return venueService.deleteVenue(id);
    }
}
