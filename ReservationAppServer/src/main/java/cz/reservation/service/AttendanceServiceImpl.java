package cz.reservation.service;

import cz.reservation.constant.BookingStatus;
import cz.reservation.constant.EventStatus;
import cz.reservation.dto.AttendanceDto;
import cz.reservation.dto.mapper.AttendanceMapper;
import cz.reservation.entity.AttendanceEntity;
import cz.reservation.entity.BookingEntity;
import cz.reservation.entity.repository.AttendanceRepository;
import cz.reservation.entity.repository.BookingRepository;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.serviceinterface.AttendanceService;
import cz.reservation.service.serviceinterface.PackageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceMapper attendanceMapper;

    private final AttendanceRepository attendanceRepository;

    private final BookingRepository bookingRepository;

    private final PackageService packageService;

    private static final String SERVICE_NAME = "attendance";

    @Override
    @Transactional
    public ResponseEntity<AttendanceDto> createAttendance(AttendanceDto attendanceDto) {
        var entityToSave = attendanceMapper.toEntity(attendanceDto);
        var relatedBooking = bookingRepository.findById(attendanceDto.booking().id()).orElseThrow(
                () -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage("Booking", attendanceDto.booking().id())));

        //Sets FK
        setForeignKeys(entityToSave, attendanceDto);

        //Checking if player was present, otherwise if his absence was excused properly
        presenceChecking(attendanceDto, relatedBooking);

        //Actualizes used slots in package if exists
        actualizePackageIfExists(attendanceDto);

        var savedEntity = attendanceRepository.save(entityToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attendanceMapper.toDto(savedEntity));


    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<AttendanceDto> getAttendance(Long id) {

        return ResponseEntity
                .ok(attendanceMapper.toDto(attendanceRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<AttendanceDto>> getAllAttendances() {
        var allAttendances = attendanceRepository.findAll();
        if (allAttendances.isEmpty()) {
            throw new EmptyListException(emptyListMessage(SERVICE_NAME));
        } else {
            return ResponseEntity
                    .ok(allAttendances
                            .stream()
                            .map(attendanceMapper::toDto)
                            .toList());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editAttendance(AttendanceDto attendanceDto, Long id) {

        if (!attendanceRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToUpdate = attendanceRepository.getReferenceById(id);
            attendanceMapper.updateEntity(entityToUpdate, attendanceDto);
            setForeignKeys(entityToUpdate, attendanceDto);

            return ResponseEntity.ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED)));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteAttendance(Long id) {
        if (attendanceRepository.existsById(id)) {
            attendanceRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }

    private void presenceChecking(AttendanceDto attendanceDto, BookingEntity relatedBooking) {
        if (attendanceDto.present().equals(Boolean.FALSE) &&
                !relatedBooking.getBookingStatus().equals(BookingStatus.CANCELED)) {
            relatedBooking.setBookingStatus(BookingStatus.NO_SHOW);
        }
    }

    //TODO: What if used slots are bigger that total number of slots
    private void actualizePackageIfExists(AttendanceDto attendanceDto) {
        var hoursUsed = attendanceDto.booking().trainingSlot().endAt().getHour() -
                attendanceDto.booking().trainingSlot().startAt().getHour();
        var relatedPlayer = attendanceDto.booking().player();
        var relatedPackage = packageService.getPackageByPlayerId(relatedPlayer.id());
        relatedPackage.ifPresent(packageEntity -> packageEntity.setSlotUsed(packageEntity.getSlotUsed() + hoursUsed));
    }


    private void setForeignKeys(AttendanceEntity target, AttendanceDto source) {
        target.setBooking(bookingRepository
                .findById(source.booking().id())
                .orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                                "booking", source.booking().id()))));
    }


}