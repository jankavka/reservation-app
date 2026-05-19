package cz.reservation.controller;

import cz.reservation.dto.VenueDto;
import cz.reservation.service.serviceinterface.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/venue")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<VenueDto> createVenue(
            @RequestPart("venue") @Valid VenueDto venueDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(venueService.createVenue(venueDto, file));
    }


    @GetMapping("/{id}")
    public VenueDto getVenue(@PathVariable Long id) {
        return venueService.getVenue(id);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editVenue(
            @PathVariable Long id,
            @RequestPart("venue") @Valid VenueDto venueDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        venueService.editVenue(venueDto, id, file);
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
