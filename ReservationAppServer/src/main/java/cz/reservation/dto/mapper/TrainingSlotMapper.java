package cz.reservation.dto.mapper;

import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.entity.TrainingSlotEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingSlotMapper {

    TrainingSlotDto toDto(TrainingSlotEntity trainingSlotsEntity);

    TrainingSlotEntity toEntity(TrainingSlotDto trainingSlotsDto);
}
