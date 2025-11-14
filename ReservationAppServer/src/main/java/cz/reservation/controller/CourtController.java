package cz.reservation.controller;

import cz.reservation.dto.CourtDto;
import cz.reservation.service.serviceinterface.CourtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/court")
public class CourtController {

    private final CourtService courtService;

    @Autowired
    public CourtController(CourtService courtService){
        this.courtService = courtService;
    }

    @PostMapping
    public ResponseEntity<CourtDto> createCourt(@RequestBody @Valid CourtDto courtDto){
        return courtService.createCourt(courtDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtDto> showCourt(@PathVariable Long id){
        return courtService.getCourt(id);
    }

    @GetMapping
    public ResponseEntity<List<CourtDto>> showAllCourts(){
        return courtService.getAllCourts();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCourt(@PathVariable Long id){
        return courtService.deleteCourt(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> editCourt(@RequestBody @Valid CourtDto courtDto, @PathVariable Long id){
        return courtService.editCourt(courtDto, id);
    }
}
