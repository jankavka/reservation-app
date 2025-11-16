package cz.reservation.dto.mapper;

import cz.reservation.dto.BookingDto;
import cz.reservation.entity.BookingEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingEntity toEntity(BookingDto bookingDto);

    BookingDto toDto(BookingEntity bookingEntity);
}
