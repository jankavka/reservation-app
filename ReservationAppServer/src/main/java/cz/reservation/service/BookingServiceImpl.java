package cz.reservation.service;

import cz.reservation.constant.BookingStatus;
import cz.reservation.constant.EventStatus;
import cz.reservation.dto.BookingDto;
import cz.reservation.dto.CreatedBookingDto;
import cz.reservation.dto.mapper.BookingMapper;
import cz.reservation.entity.BookingEntity;
import cz.reservation.entity.TrainingSlotEntity;
import cz.reservation.entity.repository.BookingRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.service.exception.LateBookingCancelingException;
import cz.reservation.service.exception.TrainingAlreadyStartedException;
import cz.reservation.service.serviceinterface.BookingService;
import cz.reservation.service.serviceinterface.TrainingSlotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;

    private final BookingRepository bookingRepository;

    private final PlayerRepository playerRepository;

    private final TrainingSlotService trainingSlotService;

    private final ApplicationEventPublisher eventPublisher;

    private static final String SERVICE_NAME = "booking";

    private static final String MESSAGE = "message";

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto) {

        var entityToSave = bookingMapper.toEntity(bookingDto);
        var relatedTrainingSlot = trainingSlotService.getTrainingSlotEntity(bookingDto.trainingSlot().id());


        entityToSave.setBookedAt(LocalDateTime.now());

        setForeignKeys(entityToSave, bookingDto);

        checkRelatedTrainingSlotIsBookable(relatedTrainingSlot);

        setBookingStatusDueToCurrentCapacity(relatedTrainingSlot, entityToSave);

        var savedEntity = bookingRepository.save(entityToSave);

        eventPublisher.publishEvent(new CreatedBookingDto(this, savedEntity));

        return bookingMapper.toDto(savedEntity);

    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(Long id) {

        return bookingMapper.toDto(bookingRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id))));


    }

    @Override
    @Transactional
    public void editBooking(BookingDto bookingDto, Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToUpdate = bookingRepository.getReferenceById(id);
            var relatedTrainingSlot = trainingSlotService.getTrainingSlotEntity(bookingDto.trainingSlot().id());

            if (bookingDto.bookingStatus().equals(BookingStatus.NO_SHOW) ||
                    bookingDto.bookingStatus().equals(BookingStatus.CONFIRMED)) {
                throw new IllegalArgumentException(
                        "Current user doesn't have rights to set booking status as NO_SHOW, or CONFIRMED");
            }

            //Check if incoming bookingDto is set to CANCELED and the beginning of related training slot starts at more
            //than 24 hours before now. If remaining time start of training slot is less than 24 hours, than client can
            //not change the status and full price will be charged
            if (bookingDto.bookingStatus().equals(BookingStatus.CANCELED) &&
                    LocalDateTime.now().isBefore(relatedTrainingSlot.getStartAt().minusHours(24))) {

                bookingMapper.updateEntity(entityToUpdate, bookingDto);
            } else {
                throw new LateBookingCancelingException(
                        "Can not cancel reservation less than 24 hours before training slot");
            }


        }
    }

    @Override
    @Transactional
    public void editBookingAsAdmin(BookingDto bookingDto, Long id) {

        if (bookingRepository.existsById(id)) {
            var entityToUpdate = bookingRepository.getReferenceById(id);
            bookingMapper.updateEntity(entityToUpdate, bookingDto);
            setForeignKeys(entityToUpdate, bookingDto);
        } else {

            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings() {
        return bookingRepository
                .findAll()
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            bookingRepository.getReferenceById(id).setBookingStatus(BookingStatus.CANCELED);

        }
    }

    @Override
    public Integer usedCapacityOfRelatedTrainingSlot(Long trainingSlotId) {
        return trainingSlotService.getUsedCapacityOfRelatedTrainingSlot(trainingSlotId);
    }

    private void setForeignKeys(
            BookingEntity entityToSave,
            BookingDto bookingDto) {

        var relatedTrainingSlot = trainingSlotService.getTrainingSlotEntity(bookingDto.trainingSlot().id());

        entityToSave.setPlayer(playerRepository.getReferenceById(bookingDto.player().id()));
        entityToSave.setTrainingSlot(relatedTrainingSlot);
    }

    private void setBookingStatusDueToCurrentCapacity(
            TrainingSlotEntity relatedTrainingSlot,
            BookingEntity entityToSave) {

        if (usedCapacityOfRelatedTrainingSlot(relatedTrainingSlot.getId()) < relatedTrainingSlot.getCapacity()) {
            entityToSave.setBookingStatus(BookingStatus.CONFIRMED);
        } else {
            entityToSave.setBookingStatus(BookingStatus.WAITLIST);
        }
    }

    private void checkRelatedTrainingSlotIsBookable(TrainingSlotEntity relatedTrainingSlot) {
        if (relatedTrainingSlot.getStartAt().isBefore(LocalDateTime.now())) {
            throw new TrainingAlreadyStartedException("Training slot which already started can't be reserved");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByPlayerIdAndMonth(Long playerId, Month month, int year) {
        var yearMonth = YearMonth.of(year, month);
        var startDate = yearMonth.atDay(1).atStartOfDay();
        var endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        return bookingRepository
                .findByPlayerIdAndTrainingSlotStartAtBetween(playerId, startDate, endDate)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Scheduled(cron = "0 0 1 1 * ?", zone = "Europe/Prague")
    @Transactional
    public void deleteOldBookings() {
        var cutoffDate = LocalDateTime.now().minusMonths(3);
        bookingRepository.deleteByBookedAtBefore(cutoffDate);
    }

}