package cz.reservation.service.serviceinterface;

import cz.reservation.dto.BookingDto;
import cz.reservation.entity.filter.BookingFilter;

import java.time.Month;
import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto);

    BookingDto getBooking(Long id);

    void editBooking(BookingDto bookingDto, Long id);

    void editBookingAsAdmin(BookingDto bookingDto, Long id);

    List<BookingDto> getAllBookings(BookingFilter bookingFilter);

    void deleteBooking(Long id);

    Integer usedCapacityOfRelatedTrainingSlot(Long trainingSlotId);

    List<BookingDto> getBookingsByPlayerIdAndMonth(Long playerId, Month month, int year);
}
