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

        var pricingType = invoiceSummaryDto.pricingType();

        var entityToSave = invoiceSummaryMapper.toEntity(invoiceSummaryDto);
        var relatedUser = userService.getUserEntity(invoiceSummaryDto.user().id());

        var allBookingIdsByUser = getAllBookingIdsByUser(relatedUser, invoiceSummaryDto);

        setPriceToEntity(pricingType, entityToSave, allBookingIdsByUser, invoiceSummaryDto);


        //Setting id if entity with selected month is already in database
        if (invoiceSummaryRepository.summaryOfCurrentMonth(invoiceSummaryDto.month().getValue()) != null) {
            entityToSave.setId(invoiceSummaryRepository
                    .getIdOfCurrentMonthSummary(invoiceSummaryDto.month().getValue()));
        }

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

    private void setForeignKeys(InvoiceSummaryEntity target, InvoiceSummaryDto source) {
        target.setUser(userService.getUserEntity(source.user().id()));
    }


    private List<Long> getAllBookingIdsByUser(UserEntity relatedUser, InvoiceSummaryDto invoiceSummaryDto) {
        var allPlayersByUser = relatedUser.getPlayers();
        return allPlayersByUser.stream()
                .flatMap(playerEntity -> playerEntity.getBookings().stream())
                .filter(bookingEntity -> bookingEntity
                        .getTrainingSlot()
                        .getStartAt()
                        .getMonth()
                        .equals(invoiceSummaryDto.month()))
                .map(BookingEntity::getId)
                .toList();
    }

    private void setPriceToEntity(
            PricingType pricingType,
            InvoiceSummaryEntity entityToSave,
            List<Long> allBookingIdsByUser,
            InvoiceSummaryDto invoiceSummaryDto) {

        if (pricingType == PricingType.PER_SLOT) {
            //Computing final price of invoice summary
            var price = getFinalPrice(allBookingIdsByUser);

            //setting price ad total amount
            entityToSave.setTotalCentsAmount(price);

        } else if (pricingType == PricingType.MONTHLY) {
            var pricingRuleDto = invoiceSummaryDto.rule();
            if (pricingRuleDto != null) {
                var price = getFinalPrice(pricingRuleDto);
                entityToSave.setTotalCentsAmount(price);
            } else {
                throw new NullPointerException("With monthly pricing type pricing rule must not be null");
            }


        }
    }

    private Integer getFinalPrice(List<Long> allBookingIdByUser) {
        return allBookingIdByUser
                .stream()
                .mapToInt(bookingService::getPriceForBooking)
                .sum();
    }

    private Integer getFinalPrice(PricingRuleDto pricingRuleDto) {
        return pricingEngine.computePriceOfMonthlyPricingType(pricingRuleDto);
    }
}
