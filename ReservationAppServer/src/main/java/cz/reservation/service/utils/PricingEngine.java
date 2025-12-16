package cz.reservation.service.utils;

import cz.reservation.dto.BookingDto;

public interface PricingEngine {

    Integer computePriceOfSingleBooking(BookingDto bookingDto);


}
