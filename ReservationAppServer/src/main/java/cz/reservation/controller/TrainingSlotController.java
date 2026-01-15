package cz.reservation.controller;

import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.service.serviceinterface.TrainingSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-slot")
@RequiredArgsConstructor
public class TrainingSlotController {

    private final TrainingSlotService trainingSlotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingSlotDto createTrainingSlot(@RequestBody @Valid TrainingSlotDto trainingSlotDto) {
        return trainingSlotService.createTrainingSlot(trainingSlotDto);
    }

    @GetMapping("/{id}")
    public TrainingSlotDto getTrainingSlot(@PathVariable Long id) {
        return trainingSlotService.getTrainingSlot(id);
    }

    @GetMapping
    public List<TrainingSlotDto> getAllTrainingSlots() {
        return trainingSlotService.getAllTrainingSlots();
    }

    @GetMapping("/group/{groupId}")
    public List<TrainingSlotDto> getAllSlotsByGroupId(@PathVariable Long groupId) {
        return trainingSlotService.getAllTrainingSlotsByGroupId(groupId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editTrainingSlot(@RequestBody @Valid TrainingSlotDto trainingSlotDto, @PathVariable Long id) {
        trainingSlotService.editTrainingSlot(trainingSlotDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainingSlot(@PathVariable Long id) {
        trainingSlotService.deleteTrainingSlot(id);
    }
}
