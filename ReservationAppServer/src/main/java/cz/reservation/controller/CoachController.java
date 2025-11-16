package cz.reservation.controller;

import cz.reservation.dto.CoachDto;
import cz.reservation.service.serviceinterface.CoachService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach")
public class CoachController {

    private final CoachService coachService;

    @Autowired
    private CoachController(CoachService coachService){
        this.coachService = coachService;
    }

    @PostMapping
    public ResponseEntity<CoachDto> createCoach(@RequestBody @Valid CoachDto coachDto){
        return coachService.createCoach(coachDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoachDto> getCoach(@PathVariable Long id){
        return coachService.getCoach(id);
    }

    @GetMapping
    public ResponseEntity<List<CoachDto>> getAllCoaches(){
        return coachService.getAllCoaches();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCoach(@PathVariable Long id){
        return coachService.deleteCoach(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CoachDto> editCoach(@RequestBody @Valid CoachDto coachDto, @PathVariable Long id){
        return coachService.editCoach(coachDto,id);
    }
}
