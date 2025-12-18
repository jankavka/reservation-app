package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.PricingType;
import cz.reservation.dto.InvoiceSummaryDto;
import cz.reservation.dto.PricingRuleDto;
import cz.reservation.dto.mapper.InvoiceSummaryMapper;
import cz.reservation.entity.BookingEntity;
import cz.reservation.entity.InvoiceSummaryEntity;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.InvoiceSummaryRepository;
import cz.reservation.service.serviceinterface.BookingService;
import cz.reservation.service.serviceinterface.InvoiceSummaryService;
import cz.reservation.service.serviceinterface.UserService;
import cz.reservation.service.utils.PricingEngine;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class InvoiceSummaryServiceImpl implements InvoiceSummaryService {

    private final InvoiceSummaryRepository invoiceSummaryRepository;

    private final InvoiceSummaryMapper invoiceSummaryMapper;

    private final UserService userService;

    private final BookingService bookingService;

    private final PricingEngine pricingEngine;

    private static final String SERVICE_NAME = "invoice summary";

    @Override
    @Transactional
    public ResponseEntity<InvoiceSummaryDto> createSummary(InvoiceSummaryDto invoiceSummaryDto) {

        var entityToSave = invoiceSummaryMapper.toEntity(invoiceSummaryDto);

        setPriceToEntity(entityToSave, invoiceSummaryDto);

        replaceSummaryIfAlreadyExists(invoiceSummaryDto, entityToSave);

        entityToSave.setGeneratedAt(LocalDateTime.now());
        setForeignKeys(entityToSave, invoiceSummaryDto);
        InvoiceSummaryEntity savedEntity = invoiceSummaryRepository.save(entityToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invoiceSummaryMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<InvoiceSummaryDto> getSummary(Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invoiceSummaryMapper.toDto(invoiceSummaryRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional
    public ResponseEntity<InvoiceSummaryDto> editSummary(InvoiceSummaryDto invoiceSummaryDto, Long id) {

        var entityToSave = invoiceSummaryMapper.toEntity(invoiceSummaryDto);
        setForeignKeys(entityToSave, invoiceSummaryDto);
        entityToSave.setId(id);
        var savedEntity = invoiceSummaryRepository.save(entityToSave);
        return ResponseEntity.status(HttpStatus.OK).body(invoiceSummaryMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<InvoiceSummaryDto>> getAllSummaries() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invoiceSummaryRepository
                        .findAll()
                        .stream()
                        .map(invoiceSummaryMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<InvoiceSummaryDto>> getAllSummariesByUser(Long userId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invoiceSummaryRepository
                        .findByUserId(userId)
                        .stream()
                        .map(invoiceSummaryMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteSummary(Long id) {
        if (!invoiceSummaryRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            invoiceSummaryRepository.deleteById(id);

            var responseMessage = Map.of(
                    "message", successMessage(SERVICE_NAME, id, EventStatus.DELETED));

            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        }
    }

    /**
     * Sets foreign keys to entity
     *
     * @param target The entity for which we are setting foreign keys
     * @param source Dto with foreign keys
     */
    private void setForeignKeys(InvoiceSummaryEntity target, InvoiceSummaryDto source) {
        target.setUser(userService.getUserEntity(source.user().id()));
    }


    /**
     * Helper method. Gets ids of bookings which are connected to certain user, based on concrete month.
     *
     * @param relatedUser Entity of user connected with bookings we look for
     * @param month       Param we use for filtering entities
     * @return List of ids from booking entities which are connected to concrete user and happened in
     * concrete month
     */
    private List<Long> getAllBookingIdsByUser(UserEntity relatedUser, Month month) {
        var allPlayersByUser = relatedUser.getPlayers();
        return allPlayersByUser.stream()
                .flatMap(playerEntity -> playerEntity.getBookings().stream())
                .filter(bookingEntity -> bookingEntity
                        .getTrainingSlot()
                        .getStartAt()
                        .getMonth()
                        .equals(month))
                .map(BookingEntity::getId)
                .toList();
    }

    /**
     * Helper method. Sets price to entity
     *
     * @param target            Entity we set price for
     * @param invoiceSummaryDto Dto which contains attributes for setting price
     */
    private void setPriceToEntity(
            InvoiceSummaryEntity target,
            InvoiceSummaryDto invoiceSummaryDto) {

        var relatedUser = userService.getUserEntity(invoiceSummaryDto.user().id());
        var pricingType = invoiceSummaryDto.pricingType();
        var allBookingIdsByUser = getAllBookingIdsByUser(relatedUser, invoiceSummaryDto.month());

        if (pricingType == PricingType.PER_SLOT) {
            //Computing final price of invoice summary
            var price = getFinalPricePerSlot(allBookingIdsByUser);

            //setting price total amount
            target.setTotalCentsAmount(price);

        } else if (pricingType == PricingType.MONTHLY) {
            var pricingRuleDto = invoiceSummaryDto.rule();
            if (pricingRuleDto != null) {
                var price = getFinalPriceMonthly(pricingRuleDto);
                target.setTotalCentsAmount(price);
            } else {
                throw new NullPointerException("With monthly pricing type pricing rule must not be null");
            }
        }
    }

    /**
     * Helper method. Computes final price of invoice in case PricingType is PER_SLOT, which is default
     *
     * @param allBookingIdByUser List of all booking ids related to user in concrete month
     * @return Integer representation of invoice final price
     */
    private Integer getFinalPricePerSlot(List<Long> allBookingIdByUser) {
        return allBookingIdByUser
                .stream()
                .mapToInt(bookingService::getPriceForBooking)
                .sum();
    }

    /**
     * Helper method. Computes final price of invoice in case
     *
     * @param pricingRuleDto Dto with concrete rule information
     * @return Integer representation of invoice final price
     */
    private Integer getFinalPriceMonthly(PricingRuleDto pricingRuleDto) {
        return pricingEngine.computePriceOfMonthlyPricingType(pricingRuleDto);
    }

    /**
     * Helper method. Replaces invoice summary if already exists the one with same month attribute.
     *
     * @param source The object on which we set the target object
     * @param target On this entity we set new id if there is already an invoice summary with its month
     *               attribute
     */
    private void replaceSummaryIfAlreadyExists(InvoiceSummaryDto source, InvoiceSummaryEntity target) {
        var existingSummary = invoiceSummaryRepository.getSummaryOfCurrentMonth(source.month().getValue());

        existingSummary.ifPresent(summary -> target.setId(summary.getId()));
    }
}
