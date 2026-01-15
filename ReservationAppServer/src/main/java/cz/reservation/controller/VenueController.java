package cz.reservation.controller;

import cz.reservation.dto.VenueDto;
import cz.reservation.service.serviceinterface.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venue")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<VenueDto> createVenue(@RequestBody @Valid VenueDto venueDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.createVenue(venueDto));
    }

    @GetMapping("/{id}")
    public VenueDto getVenue(@PathVariable Long id) {
        return venueService.getVenue(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editVenue(@RequestBody @Valid VenueDto venueDto, @PathVariable Long id) {
        venueService.editVenue(venueDto, id);
    }

    @GetMapping
    public List<VenueDto> getAllVenues() {
        return venueService.getAllVenues();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
    }
}
