package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.AttendanceDto;
import cz.reservation.dto.mapper.AttendanceMapper;
import cz.reservation.entity.repository.AttendanceRepository;
import cz.reservation.entity.repository.BookingRepository;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.serviceinterface.AttendanceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceMapper attendanceMapper;

    private final AttendanceRepository attendanceRepository;

    private final BookingRepository bookingRepository;

    private static final String SERVICE_NAME = "attendance";

    public AttendanceServiceImpl(
            AttendanceMapper attendanceMapper,
            AttendanceRepository attendanceRepository,
            BookingRepository bookingRepository) {

        this.attendanceMapper = attendanceMapper;
        this.attendanceRepository = attendanceRepository;
        this.bookingRepository = bookingRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<AttendanceDto> createAttendance(AttendanceDto attendanceDto) {
        var entityToSave = attendanceMapper.toEntity(attendanceDto);
        var bookingId = attendanceDto.booking().id();

        //Checking if parent exists
        if (bookingRepository.existsById(bookingId)) {
            entityToSave.setBooking(bookingRepository.getReferenceById(bookingId));
            var savedEntity = attendanceRepository.save(entityToSave);
            return ResponseEntity.status(HttpStatus.CREATED).body(attendanceMapper.toDto(savedEntity));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("booking", bookingId));
        }


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
        if (attendanceRepository.findAll().isEmpty()) {
            throw new EmptyListException(emptyListMessage(SERVICE_NAME));
        } else {
            return ResponseEntity
                    .ok(attendanceRepository
                            .findAll()
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

            return ResponseEntity.ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED)));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteAttendance(Long id) {
        if(attendanceRepository.existsById(id)){
            attendanceRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message",successMessage(SERVICE_NAME,id,EventStatus.DELETED)));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }
}
