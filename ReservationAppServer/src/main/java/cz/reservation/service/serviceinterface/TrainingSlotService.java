package cz.reservation.service.serviceinterface;

import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.entity.TrainingSlotEntity;

import java.util.List;

public interface TrainingSlotService {

    TrainingSlotDto createTrainingSlot(TrainingSlotDto trainingSlotDto);

    TrainingSlotDto getTrainingSlot(Long id);

    TrainingSlotEntity getTrainingSlotEntity(Long id);

    List<TrainingSlotDto> getAllTrainingSlots();

    List<TrainingSlotDto> getAllTrainingSlotsByGroupId(Long groupId);

    void editTrainingSlot(TrainingSlotDto trainingSlotDto, Long id);

    void deleteTrainingSlot(Long id);

    Integer getUsedCapacityOfRelatedTrainingSlot(Long id);
}
