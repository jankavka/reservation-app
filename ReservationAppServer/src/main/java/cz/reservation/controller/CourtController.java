package cz.reservation.controller;

import cz.reservation.dto.CourtDto;
import cz.reservation.entity.filter.CourtFilter;
import cz.reservation.service.serviceinterface.CourtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/court")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CourtDto createCourt(
            @RequestPart("court") @Valid CourtDto courtDto,
            @RequestPart(name = "file", required = false) MultipartFile file) {
        return courtService.createCourt(courtDto, file);
    }

    @GetMapping("/{id}")
    public CourtDto showCourt(@PathVariable Long id) {
        return courtService.getCourt(id);
    }

    @GetMapping
    public List<CourtDto> showAllCourts(CourtFilter courtFilter) {
        return courtService.getAllCourts(courtFilter);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editCourt(
            @Valid @RequestPart("court") CourtDto courtDto,
            @PathVariable Long id,
            @RequestPart(name = "file", required = false) MultipartFile file) {
        courtService.editCourt(courtDto, id, file);
    }
}
