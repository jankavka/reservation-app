package cz.reservation.controller;

import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.service.serviceinterface.TrainingSlotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training-slot")
public class TrainingSlotController {

    private final TrainingSlotService trainingSlotService;

    public TrainingSlotController(TrainingSlotService trainingSlotService){
        this.trainingSlotService = trainingSlotService;
    }

    @PostMapping
    public ResponseEntity<TrainingSlotDto> createTrainingSlot(@RequestBody @Valid TrainingSlotDto trainingSlotDto){
        return trainingSlotService.createTrainingSlot(trainingSlotDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingSlotDto> getTrainingSlot(@PathVariable Long id){
        return trainingSlotService.getTrainingSlot(id);
    }

    @GetMapping
    public ResponseEntity<List<TrainingSlotDto>> getAllTrainingSlots(){
        return trainingSlotService.getAllTrainingSlots();
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<TrainingSlotDto>> getAllSlotsByGroupId(@PathVariable Long groupId){
        return trainingSlotService.getAllTrainingSlotsByGroupId(groupId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editTrainingSlot(
            @RequestBody @Valid TrainingSlotDto trainingSlotDto,@PathVariable Long id){

        return trainingSlotService.editTrainingSlot(trainingSlotDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> deleteTrainingSlot(@PathVariable Long id){
        return trainingSlotService.deleteTrainingSlot(id);
    }
}
