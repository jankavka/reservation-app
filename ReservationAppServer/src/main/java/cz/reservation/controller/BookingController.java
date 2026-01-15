package cz.reservation.controller;

import cz.reservation.dto.BookingDto;
import cz.reservation.service.serviceinterface.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody @Valid BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @GetMapping
    public List<BookingDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editBooking(@PathVariable Long id, @RequestBody @Valid BookingDto bookingDto) {
        bookingService.editBooking(bookingDto, id);
    }

    @Secured("ADMIN")
    @PutMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editBookingAsAdmin(@PathVariable Long id, @RequestBody @Valid BookingDto bookingDto) {
        bookingService.editBookingAsAdmin(bookingDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}
