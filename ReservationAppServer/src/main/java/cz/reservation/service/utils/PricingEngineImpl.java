package cz.reservation.service.utils;

import cz.reservation.constant.BookingStatus;
import cz.reservation.constant.PricingType;
import cz.reservation.dto.*;
import cz.reservation.service.serviceinterface.PlayerService;
import cz.reservation.service.serviceinterface.PricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PricingEngineImpl implements PricingEngine {

    private final PlayerService playerService;

    private final PricingRuleService pricingRuleService;

    private static final Integer PRIME_TIME_START = 16;

    private static final Integer PRIME_TIME_END = 20;

    private static final String SURFACE = "surface";

    private static final String INDOOR = "indoor";

    private static final String LEVEL = "level";

    private static final String SIBLINGS = "siblings";

    private static final String PRIME_TIME = "primeTime";


    /**
     * Computes total amount of cents based on defined conditions in pricing rule entity.
     *
     * @param bookingDto Data transfer object of current booking
     * @return amounts of cents as Integer
     */
    @Override
    public Integer computePriceOfSingleBooking(BookingDto bookingDto) {

        var allPricingRules = pricingRuleService.getPricingRulesByPricingType(PricingType.PER_SLOT);
        var timeTotal = bookingDto.trainingSlot().endAt().getHour() - bookingDto.trainingSlot().startAt().getHour();
        var hoursInPrimeTime = computeHoursInPrimeTime(bookingDto.trainingSlot());

        if ((bookingDto.bookingStatus().equals(BookingStatus.CONFIRMED)) ||
                (bookingDto.bookingStatus().equals(BookingStatus.NO_SHOW))) {

            var pricePerHour = allPricingRules.stream()
                    .filter(pricingRuleDto -> isRuleValid(pricingRuleDto, bookingDto))
                    .mapToInt(pricingRuleDto -> {
                        if (pricingRuleDto.conditions().containsKey(PRIME_TIME)) {
                            return pricingRuleDto.amountCents() * hoursInPrimeTime;
                        } else {
                            return pricingRuleDto.amountCents();
                        }
                    })
                    .sum();

            return pricePerHour * timeTotal;

        } else return 0;
    }

    @Override
    public Integer computePriceOfMonthlyPricingType(PricingRuleDto rule) {
        return rule.amountCents();
    }


    /**
     * Helper method, which sums hours in prime-time in current training slot
     *
     * @param trainingSlotDto Data transfer object of current trainingSlot
     * @return Integer presentation of summed hours in prime time
     */
    private Integer computeHoursInPrimeTime(TrainingSlotDto trainingSlotDto) {
        var hourOfStartOfSlot = trainingSlotDto.startAt().getHour();
        var hourOfEndOfSlot = trainingSlotDto.endAt().getHour();

        if (hourOfStartOfSlot < PRIME_TIME_START) {
            hourOfStartOfSlot = PRIME_TIME_START;

        }
        if (hourOfEndOfSlot > PRIME_TIME_END) {
            hourOfEndOfSlot = PRIME_TIME_END;
        }

        var hourResult = hourOfEndOfSlot - hourOfStartOfSlot;

        return Math.max(hourResult, 0);
    }


    /**
     * Helper function, which determines if rule is valid for current booking
     *
     * @param rule    PricingRuleDto object representing concrete collection of rules
     * @param booking BookingDto object representing a booking we look rules for
     * @return a boolean value which tels if conditions of the rule object will be used of not
     */
    private boolean isRuleValid(PricingRuleDto rule, BookingDto booking) {
        var relatedCourt = booking.trainingSlot().court();
        var relatedPlayer = booking.player();
        var relatedGroup = booking.trainingSlot().group();
        var relatedUser = relatedPlayer.parent();

        return isSurfaceEqualOrReturnTrueIfNotPresent(rule, relatedCourt)
                && isIndoorEqualOrReturnTrueIfNotPresent(rule, relatedCourt)
                && isLevelEqualOrReturnTrueIfNotPresent(rule, relatedGroup)
                && isSiblingsRelevantOrReturnTrueIfNotPresent(rule, relatedUser)
                && isPrimeTimeRelevantOrReturnTrueIfNotPresent(rule, booking);
    }


    /**
     * Helper function which, looks for "surface" condition in rule.conditions(). Function
     * returns false only in case "surface" condition is present in rule.conditions() and
     * the value is not equal to the one in court.surface().
     *
     * @param rule  PricingRuleDto object representing concrete collection of rules
     * @param court CourtDto object representing a court with specific surface attribute
     * @return boolean value according to comparison of "surface" condition and court.surface(), or
     * true if "surface" condition is not present.
     */
    private boolean isSurfaceEqualOrReturnTrueIfNotPresent(PricingRuleDto rule, CourtDto court) {
        if (rule.conditions().containsKey(SURFACE)) {
            return rule.conditions()
                    .get(SURFACE.toLowerCase())
                    .equals(court.surface().getCode().toLowerCase());
        }
        return true;
    }


    /**
     * Helper function which, looks for "indoor" condition in rule.conditions(). Function
     * returns false only in case "indoor" condition is present in rule.conditions() and
     * the value is not equal to the one in court.indoor()
     *
     * @param rule  PricingRuleDto object representing concrete collection of rules
     * @param court CourtDto object representing a court with specific "indoor" attribute
     * @return boolean value according to comparison of "court" condition and court.court(), or
     * true if "court" condition is not present.
     */
    private boolean isIndoorEqualOrReturnTrueIfNotPresent(PricingRuleDto rule, CourtDto court) {
        if (rule.conditions().containsKey(INDOOR)) {
            return rule.conditions()
                    .get(INDOOR).toString()
                    .equals(court.indoor().toString());
        }
        return true;
    }

    /**
     * Helper function which, looks for "level" condition in rule.conditions(). Function
     * returns false only in case "level" condition is present in rule.conditions() and
     * the value is not equal to the one in court.level()
     *
     * @param rule  PricingRuleDto object representing concrete collection of rules
     * @param group GroupDto object representing a "group" with a specific level attribute
     * @return boolean value according to comparison of "level" condition and court.level(), or
     * true if "level" condition is not present.
     */
    private boolean isLevelEqualOrReturnTrueIfNotPresent(PricingRuleDto rule, GroupDto group) {
        if (rule.conditions().containsKey(LEVEL)) {
            return rule.conditions()
                    .get(LEVEL.toLowerCase())
                    .equals(group.level().getActualLevel().toLowerCase());
        }
        return true;
    }

    /**
     * Helper function which, looks for "siblings" condition in rule.conditions(). Function
     * returns false only in case "siblings" condition is present in rule.conditions() and
     * the number of all players connected to the user is not higher than 1.
     *
     * @param rule PricingRuleDto object representing concrete collection of rules
     * @param user UserDto object representing a concrete "user"
     * @return boolean value according to fact there is more than one player related to user, or true
     * if "siblings" condition not present
     */
    private boolean isSiblingsRelevantOrReturnTrueIfNotPresent(PricingRuleDto rule, UserDto user) {
        if (rule.conditions().containsKey(SIBLINGS)) {
            return playerService.getPlayersEntitiesByParentId(user.id()).size() > 1;
        }
        return true;
    }

    /**
     * Helper function, looks for "primeTime" condition in rule.conditions(). Function
     * return false only in case "primeTime" conditions is present in rule.conditions() and
     * the current booking doesn't interfere to time between PRIME_TIME_START and PRIME_TIME_END
     *
     * @param rule    PricingRuleDto object representing concrete collection of rules
     * @param booking BookingDto object representing a current "booking"
     * @return boolean value according to fact booking time interferes to time between PRIME_TIME_START
     * and PRIME_TIME_END, or true of "primeTime" condition is not present
     */
    private boolean isPrimeTimeRelevantOrReturnTrueIfNotPresent(PricingRuleDto rule, BookingDto booking) {
        if (rule.conditions().containsKey(PRIME_TIME)) {
            var startTime = booking.trainingSlot().startAt();
            var endTime = booking.trainingSlot().endAt();

            return (endTime.getHour() > PRIME_TIME_START &&
                    startTime.getHour() < PRIME_TIME_END);
        }
        return true;
    }
}