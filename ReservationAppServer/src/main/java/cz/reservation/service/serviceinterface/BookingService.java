package cz.reservation.service.serviceinterface;

import cz.reservation.dto.BookingDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BookingService {

    ResponseEntity<BookingDto> createBooking(BookingDto bookingDto);

    ResponseEntity<BookingDto> getBooking(Long id);

    ResponseEntity<Map<String, String>> editBooking(BookingDto bookingDto, Long id);

    ResponseEntity<Map<String, String>> editBookingAsAdmin(BookingDto bookingDto, Long id);

    ResponseEntity<List<BookingDto>> getAllBookings();

    ResponseEntity<Map<String,String>> deleteBooking(Long id);

    Integer getPriceForBooking(Long bookingId);

    Integer usedCapacityOfRelatedTrainingSlot(Long trainingSlotId);


}
