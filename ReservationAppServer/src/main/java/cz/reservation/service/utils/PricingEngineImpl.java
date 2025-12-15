package cz.reservation.service.utils;

import cz.reservation.constant.BookingStatus;
import cz.reservation.dto.*;
import cz.reservation.entity.PricingRuleEntity;
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
     * Computes total amount of cents on defined conditions on pricing rule entity.
     *
     * @param bookingDto Data transfer object of current booking
     * @return amounts of cents as Integer
     */
    @Override
    public Integer computePriceOfSingleBooking(BookingDto bookingDto) {

        var allPricingRules = pricingRuleService.getAllPricingRulesEntities();
        var timeTotal = bookingDto.trainingSlot().endAt().getHour() - bookingDto.trainingSlot().startAt().getHour();


        if ((bookingDto.bookingStatus().equals(BookingStatus.CONFIRMED)) ||
                (bookingDto.bookingStatus().equals(BookingStatus.NO_SHOW))) {

            var priceForOneHour = allPricingRules.stream()
                    //Filtering conditions with surface mentioned
                    .filter(pricingRuleEntity -> hasCondition(SURFACE, pricingRuleEntity, bookingDto))
                    //Filtering conditions with indoor mentioned
                    .filter(pricingRuleEntity -> hasCondition(INDOOR, pricingRuleEntity, bookingDto))
                    //Filtering conditions with level mentioned
                    .filter(pricingRuleEntity -> hasCondition(LEVEL, pricingRuleEntity, bookingDto))
                    //Filtering conditions with siblings mentioned
                    .filter(pricingRuleEntity -> hasCondition(SIBLINGS, pricingRuleEntity, bookingDto))
                    //Filtering conditions with primeTime mentioned
                    .filter(pricingRuleEntity -> hasCondition(PRIME_TIME, pricingRuleEntity, bookingDto))
                    .mapToInt(PricingRuleEntity::getAmountCents)
                    .sum();

            var hoursInPrimeTime = computeHoursInPrimeTime(bookingDto.trainingSlot());

            return priceForOneHour * timeTotal;

        } else return 0;
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
     * Helper method, which looks for conditions represented by key parameter, present in
     * related PricingRuleEntity and then checks for congruence in current booking.
     *
     * @param key        String representation of condition
     * @param entity     Pricing rule entity as a holder of condition
     * @param bookingDto Data transfer object of current booking
     * @return boolean value if condition will be applied
     */
    private boolean hasCondition(
            String key,
            PricingRuleEntity entity,
            BookingDto bookingDto) {

        var relatedCourt = bookingDto.trainingSlot().court();
        var relatedPlayer = bookingDto.player();
        var relatedGroup = bookingDto.trainingSlot().group();
        var relatedUser = relatedPlayer.parent();

        switch (key) {
            case SURFACE -> {
                if (entity.getConditions().containsKey(SURFACE)) {
                    return entity.getConditions()
                            .get(SURFACE.toLowerCase())
                            .equals(relatedCourt.surface().getCode().toLowerCase());
                }
                return true;
            }
            case INDOOR -> {
                if (entity.getConditions().containsKey(INDOOR)) {
                    return entity.getConditions()
                            .get(INDOOR).toString()
                            .equals(relatedCourt.indoor().toString());
                }
                return true;
            }
            case LEVEL -> {
                if (entity.getConditions().containsKey(LEVEL)) {
                    return entity.getConditions()
                            .get(LEVEL.toLowerCase())
                            .equals(relatedGroup.level().getActualLevel().toLowerCase());
                }
                return true;
            }
            case SIBLINGS -> {
                if (entity.getConditions().containsKey(SIBLINGS)) {
                    return playerService.getPlayersEntitiesByParentId(relatedUser.id()).size() > 1;
                }
                return true;
            }
            case PRIME_TIME -> {
                if (entity.getConditions().containsKey(PRIME_TIME)) {
                    var startTime = bookingDto.trainingSlot().startAt();
                    var endTime = bookingDto.trainingSlot().endAt();

                    return (endTime.getHour() > PRIME_TIME_START &&
                            startTime.getHour() < PRIME_TIME_END);
                }
                return true;
            }
            default -> {
                return true;
            }
        }
    }
}