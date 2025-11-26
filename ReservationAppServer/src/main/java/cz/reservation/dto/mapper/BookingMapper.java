package cz.reservation.dto.mapper;

import cz.reservation.dto.BookingDto;
import cz.reservation.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingEntity toEntity(BookingDto bookingDto);

    BookingDto toDto(BookingEntity bookingEntity);

    void updateEntity(@MappingTarget BookingEntity target, BookingDto bookingDto);
}
