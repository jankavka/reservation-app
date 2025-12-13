package cz.reservation.service.utils;

import cz.reservation.constant.BookingStatus;
import cz.reservation.dto.*;
import cz.reservation.entity.PricingRuleEntity;
import cz.reservation.service.serviceinterface.PlayerService;
import cz.reservation.service.serviceinterface.PricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PricingEngine {

    private PlayerService playerService;

    private PricingRuleService pricingRuleService;

    private static final Integer PRIME_TIME_START = 16;

    private static final Integer PRIME_TIME_END = 20;

    private static final String SURFACE = "surface";

    private static final String INDOOR = "indoor";

    private static final String LEVEL = "level";

    private static final String SIBLINGS = "siblings";

    private static final String START_TIME = "startTime";

    private static final String END_TIME = "endTime";


    /**
     * Computes total amount of cents on defined conditions on pricing rule entity.
     *
     * @param bookingDto Data transfer object of current booking
     * @return amounts of cents as Integer
     */
    public Integer computePriceOfSingleBooking(BookingDto bookingDto) {

        var allPricingRules = pricingRuleService.getAllPricingRulesEntities();

        if ((bookingDto.bookingStatus().equals(BookingStatus.CONFIRMED)) ||
                (bookingDto.bookingStatus().equals(BookingStatus.NO_SHOW))) {

            return allPricingRules.stream()
                    //Filtering conditions with surface mentioned
                    .filter(pricingRuleEntity -> hasCondition(SURFACE, pricingRuleEntity, bookingDto))
                    //Filtering conditions with indoor mentioned
                    .filter(pricingRuleEntity -> hasCondition(INDOOR, pricingRuleEntity, bookingDto))
                    //Filtering conditions with level mentioned
                    .filter(pricingRuleEntity -> hasCondition(LEVEL, pricingRuleEntity, bookingDto))
                    //filtering conditions with siblings mentioned
                    .filter(pricingRuleEntity -> hasCondition(SIBLINGS, pricingRuleEntity, bookingDto))
                    //filtering conditions with startTime and endTime mentioned
                    .filter(pricingRuleEntity -> hasCondition(START_TIME, pricingRuleEntity, bookingDto))
                    .mapToInt(PricingRuleEntity::getAmountCents)
                    .sum();


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

        if (hourOfStartOfSlot < 16) {
            hourOfStartOfSlot = 16;

        }
        if (hourOfEndOfSlot > 20) {
            hourOfEndOfSlot = 20;
        }

        var hourResult = hourOfEndOfSlot - hourOfStartOfSlot;

        return Math.max(hourResult, 0);
    }

    /**
     * Helper method, which looks for conditions represented by key parameter, present in
     * related PricingRuleEntity.
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
        var relatedTrainingSlot = bookingDto.trainingSlot();
        var relatedGroup = bookingDto.trainingSlot().group();
        var relatedUser = relatedPlayer.parent();

        switch (key) {
            case SURFACE -> {
                if (entity.getConditions().containsKey(SURFACE)) {
                    return entity.getConditions().get(SURFACE).equals(relatedCourt.surface());
                }
                return true;
            }
            case INDOOR -> {
                if (entity.getConditions().containsKey(INDOOR)) {
                    return entity.getConditions().get(INDOOR)
                            .equals(relatedCourt.indoor());
                }
                return true;
            }
            case LEVEL -> {
                if (entity.getConditions().containsKey(LEVEL)) {
                    return entity.getConditions().get(LEVEL).equals(relatedGroup.level());
                }
                return true;
            }
            case SIBLINGS -> {
                if (entity.getConditions().containsKey(SIBLINGS)) {
                    return playerService.getPlayersEntitiesByParentId(relatedUser.id()).size() > 1;
                }
                return true;
            }
            case START_TIME -> {
                if (entity.getConditions().containsKey(START_TIME) &&
                        entity.getConditions().containsKey(END_TIME)) {
                    var startTime = (String) entity.getConditions().get(START_TIME);
                    var endTime = (String) entity.getConditions().get(END_TIME);

                    entity.setAmountCents(entity.getAmountCents() * computeHoursInPrimeTime(relatedTrainingSlot));

                    return (LocalDateTime.parse(endTime).getHour() > PRIME_TIME_START &&
                            LocalDateTime.parse(startTime).getHour() < PRIME_TIME_END);
                }
                return true;
            }
            default -> {
                return true;
            }


        }
    }
}
