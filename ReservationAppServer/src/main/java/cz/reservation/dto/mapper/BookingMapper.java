package cz.reservation.dto.mapper;

import cz.reservation.dto.BookingDto;
import cz.reservation.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TrainingSlotMapper.class, PlayerMapper.class})
public interface BookingMapper {

    BookingEntity toEntity(BookingDto bookingDto);

    BookingDto toDto(BookingEntity bookingEntity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget BookingEntity target, BookingDto bookingDto);
}
