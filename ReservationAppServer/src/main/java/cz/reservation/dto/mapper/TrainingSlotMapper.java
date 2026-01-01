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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "court", ignore = true)
    @Mapping(target = "courtBlocking", ignore = true)
    void updateEntity(@MappingTarget TrainingSlotEntity target, TrainingSlotDto source);
}
