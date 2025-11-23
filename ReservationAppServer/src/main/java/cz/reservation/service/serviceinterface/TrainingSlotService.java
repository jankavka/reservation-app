package cz.reservation.service.serviceinterface;

import cz.reservation.dto.TrainingSlotDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface TrainingSlotService {

    ResponseEntity<TrainingSlotDto> createTrainingSlot(TrainingSlotDto trainingSlotDto);

    ResponseEntity<TrainingSlotDto> getTrainingSlot(Long id);

    ResponseEntity<List<TrainingSlotDto>> getAllTrainingSlots();

    ResponseEntity<Map<String, String>> editTrainingSlot(TrainingSlotDto trainingSlotDto, Long id);

    ResponseEntity<Map<String, String>> deleteTrainingSlot(Long id);
}
