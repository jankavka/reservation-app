package cz.reservation.service;

import cz.reservation.constant.BookingStatus;
import cz.reservation.constant.EventStatus;
import cz.reservation.dto.BookingDto;
import cz.reservation.dto.mapper.BookingMapper;
import cz.reservation.entity.repository.BookingRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.entity.repository.TrainingSlotRepository;
import cz.reservation.service.serviceinterface.BookingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;

    private final BookingRepository bookingRepository;

    private final PlayerRepository playerRepository;

    private final TrainingSlotRepository trainingSlotRepository;

    private static final String SERVICE_NAME = "booking";


    public BookingServiceImpl(
            BookingMapper bookingMapper,
            BookingRepository bookingRepository,
            PlayerRepository playerRepository,
            TrainingSlotRepository trainingSlotRepository
    ) {
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
        this.playerRepository = playerRepository;
        this.trainingSlotRepository = trainingSlotRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<BookingDto> createBooking(BookingDto bookingDto) {

        var entityToSave = bookingMapper.toEntity(bookingDto);
        var relatedTrainingSlot = trainingSlotRepository.getReferenceById(bookingDto.trainingSlot().id());
        entityToSave.setBookedAt(new Date());
        entityToSave.setPlayer(playerRepository.getReferenceById(bookingDto.player().id()));
        entityToSave.setTrainingSlot(relatedTrainingSlot);
        entityToSave.setBookingStatus(BookingStatus.CONFIRMED);
        entityToSave.setBookedAt(new Date());
        var savedEntity = bookingRepository.save(entityToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<BookingDto> getBooking(Long id) {

        return ResponseEntity
                .ok(bookingMapper.toDto(bookingRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id)))));


    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editBooking(BookingDto bookingDto, Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToUpdate = bookingRepository.getReferenceById(id);
            bookingMapper.updateEntity(entityToUpdate, bookingDto);

            return ResponseEntity.ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED)));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity
                .ok(bookingRepository
                        .findAll()
                        .stream()
                        .map(bookingMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            bookingRepository.deleteById(id);
            return ResponseEntity
                    .ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }
    }


}
