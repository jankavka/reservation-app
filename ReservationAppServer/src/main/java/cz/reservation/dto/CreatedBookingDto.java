package cz.reservation.dto;

import cz.reservation.entity.BookingEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreatedBookingDto extends ApplicationEvent {

    private final transient BookingEntity bookingEntity;

    public CreatedBookingDto(Object source, BookingEntity entity) {
        super(source);
        this.bookingEntity = entity;
    }
}
