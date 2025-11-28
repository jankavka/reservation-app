package cz.reservation.controller;

import cz.reservation.dto.BookingDto;
import cz.reservation.service.serviceinterface.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody @Valid BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editBooking(
            @PathVariable Long id, @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.editBooking(bookingDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBooking(@PathVariable Long id) {
        return bookingService.deleteBooking(id);
    }
}
