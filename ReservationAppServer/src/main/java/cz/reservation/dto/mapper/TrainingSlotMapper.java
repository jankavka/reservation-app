package cz.reservation.dto.mapper;

import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.entity.TrainingSlotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrainingSlotMapper {

    @Mapping(source = "courtBlocking.id", target = "courtBlockingId")
    TrainingSlotDto toDto(TrainingSlotEntity trainingSlotsEntity);

    TrainingSlotEntity toEntity(TrainingSlotDto trainingSlotsDto);

    void updateEntity(@MappingTarget TrainingSlotEntity target, TrainingSlotDto source);
}
