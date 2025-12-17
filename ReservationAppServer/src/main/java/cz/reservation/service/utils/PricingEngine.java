package cz.reservation.service.utils;

import cz.reservation.dto.BookingDto;
import cz.reservation.dto.PricingRuleDto;

public interface PricingEngine {

    Integer computePriceOfSingleBooking(BookingDto bookingDto);

    Integer computePriceOfMonthlyPricingType(PricingRuleDto rule);


}
