package cz.reservation.service;

import cz.reservation.constant.BookingStatus;
import cz.reservation.constant.Handedness;
import cz.reservation.constant.SlotStatus;
import cz.reservation.dto.*;
import cz.reservation.dto.mapper.AttendanceMapper;
import cz.reservation.entity.*;
import cz.reservation.entity.filter.AttendanceFilter;
import cz.reservation.entity.repository.AttendanceRepository;
import cz.reservation.entity.repository.BookingRepository;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.serviceinterface.PackageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.emptyListMessage;
import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    AttendanceMapper attendanceMapper;

    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    PackageService packageService;

    @InjectMocks
    AttendanceServiceImpl attendanceService;

    @Test
    void shouldCreateAndReturnAttendanceDto() {
        var relatedPlayerEntity = new PlayerEntity(
                1L, "N", null, Handedness.RIGHT, null,
                null, null, null, null, null, null);
        var relatedPlayerDto = new PlayerDto(
                1L, null, null, null,
                null, null, null, null);
        var bookingDto = new BookingDto(
                null, null, relatedPlayerDto, null, null);
        var bookingEntity = new BookingEntity(
                1L, null, relatedPlayerEntity, null, null, null);
        var attendanceDto = new AttendanceDto(
                null, bookingDto, Boolean.TRUE, null, null);
        var attendanceEntityToSave = new AttendanceEntity(
                null, bookingEntity, Boolean.TRUE, null, null);
        var savedAttendanceEntity = new AttendanceEntity(
                1L, bookingEntity, Boolean.TRUE, null, null);
        var savedAttendanceDto = new AttendanceDto(
                1L, bookingDto, Boolean.TRUE, null, null);


        when(attendanceMapper.toEntity(attendanceDto)).thenReturn(attendanceEntityToSave);
        when(bookingRepository.findById(attendanceDto.booking().id())).thenReturn(Optional.of(bookingEntity));
        when(attendanceRepository.save(attendanceEntityToSave)).thenReturn(savedAttendanceEntity);
        when(attendanceMapper.toDto(savedAttendanceEntity)).thenReturn(savedAttendanceDto);

        var result = attendanceService.createAttendance(attendanceDto);

        assertEquals(savedAttendanceDto, result);

        verify(attendanceMapper).toEntity(attendanceDto);
        verify(attendanceMapper).toDto(savedAttendanceEntity);
        verify(bookingRepository).findById(attendanceDto.booking().id());
        verify(attendanceRepository).save(attendanceEntityToSave);
        verifyNoInteractions(packageService);


    }

    @Test
    void shouldThrowEntityNotFoundException_inCaseNoBooking() {
        var bookingDto = new BookingDto(null, null, null, null, null);
        var bookingEntity = new BookingEntity(
                1L, null, null, null, null, null);
        var attendanceDto = new AttendanceDto(null, bookingDto, Boolean.TRUE, null, null);
        var attendanceEntity = new AttendanceEntity(null, bookingEntity, Boolean.TRUE, null, null);


        when(attendanceMapper.toEntity(attendanceDto)).thenReturn(attendanceEntity);

        var exception = assertThrows(
                EntityNotFoundException.class,
                () -> attendanceService.createAttendance(attendanceDto));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage(
                "booking", attendanceDto.booking().id()), exception.getMessage());

        verify(attendanceMapper).toEntity(attendanceDto);
        verify(bookingRepository).findById(attendanceDto.booking().id());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(attendanceMapper);
        verifyNoInteractions(attendanceRepository);

    }


    @Test
    void shouldThrowWhenPackageNotFound() {

        var startDate = LocalDateTime.of(2026, Month.AUGUST, 10, 16, 0);
        var endDate = LocalDateTime.of(2026, Month.AUGUST, 10, 18, 0);

        var relatedPlayerEntity = new PlayerEntity(
                1L, "N", null, Handedness.RIGHT, null,
                null, null, null, null, null, null);
        var packageDtoAssistant = new PackageDto(
                1L, null, "P", 10, null,
                null, null, null, null);
        var relatedTrainingSlotDto = new TrainingSlotDto(
                1L, null, null, startDate, endDate, 4,
                SlotStatus.OPEN, null, null, null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(
                1L, null, null, null, startDate, endDate,
                4, SlotStatus.OPEN, null, null, null, null);
        var relatedPlayerDto = new PlayerDto(
                1L, null, null, null,
                null, null, null, packageDtoAssistant);
        var packageEntity = new PackageEntity(
                1L, relatedPlayerEntity, "P", 10,
                null, null, null, null);
        var bookingDto = new BookingDto(
                null, relatedTrainingSlotDto, relatedPlayerDto, null, null);
        var bookingEntity = new BookingEntity(
                1L, relatedTrainingSlotEntity, relatedPlayerEntity,
                null, null, null);
        var attendanceDto = new AttendanceDto(
                null, bookingDto, Boolean.TRUE, null, null);
        var attendanceEntityToSave = new AttendanceEntity(
                null, bookingEntity, Boolean.TRUE, null, null);

        relatedPlayerEntity.setPackagee(packageEntity);


        when(attendanceMapper.toEntity(attendanceDto)).thenReturn(attendanceEntityToSave);
        when(bookingRepository.findById(attendanceDto.booking().id())).thenReturn(Optional.of(bookingEntity));
        when(packageService.getPackageByPlayerId(relatedPlayerDto.id())).thenThrow(new EntityNotFoundException("Package not found"));

        var exception = assertThrows(EntityNotFoundException.class, () -> attendanceService.createAttendance(attendanceDto));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals("Package not found", exception.getMessage());

        verify(attendanceMapper).toEntity(attendanceDto);
        verify(bookingRepository).findById(attendanceDto.booking().id());
        verify(packageService).getPackageByPlayerId(relatedPlayerDto.id());
        verifyNoMoreInteractions(attendanceMapper);
        verifyNoMoreInteractions(attendanceRepository);

    }

    @Test
    void shouldSetBookingStatusToNoShow() {

        var startDate = LocalDateTime.of(2026, Month.AUGUST, 10, 16, 0);
        var endDate = LocalDateTime.of(2026, Month.AUGUST, 10, 18, 0);
        var relatedPlayerEntity = new PlayerEntity(
                1L, "N", null, Handedness.RIGHT, null,
                null, null, null, null, null, null);
        var relatedPlayerDto = new PlayerDto(
                1L, null, null, null,
                null, null, null, null);
        var relatedTrainingSlotDto = new TrainingSlotDto(
                1L, null, null, startDate, endDate, 4, SlotStatus.OPEN,
                null, null, null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(
                1L, null, null, null, startDate, endDate,
                4, SlotStatus.OPEN, null, null, null, null);
        var bookingDto = new BookingDto(
                null, relatedTrainingSlotDto, relatedPlayerDto, BookingStatus.CONFIRMED, null);
        var bookingEntity = new BookingEntity(
                1L, relatedTrainingSlotEntity, relatedPlayerEntity,
                null, BookingStatus.CONFIRMED, null);
        var newBookingEntity = new BookingEntity(
                1L, relatedTrainingSlotEntity, relatedPlayerEntity,
                null, BookingStatus.NO_SHOW, null);
        var newBookingDto = new BookingDto(
                null, relatedTrainingSlotDto, relatedPlayerDto,
                BookingStatus.NO_SHOW, null);
        var attendanceDto = new AttendanceDto(
                null, bookingDto, Boolean.FALSE, null, null);
        var attendanceEntityToSave = new AttendanceEntity(
                null, newBookingEntity, Boolean.FALSE, null, null);
        var savedAttendanceEntity = new AttendanceEntity(
                1L, newBookingEntity, Boolean.FALSE, null, null);
        var savedAttendanceDto = new AttendanceDto(
                1L, newBookingDto, Boolean.FALSE, null, null);

        when(attendanceMapper.toEntity(attendanceDto)).thenReturn(attendanceEntityToSave);
        when(bookingRepository.findById(attendanceDto.booking().id())).thenReturn(Optional.of(bookingEntity));
        when(attendanceRepository.save(attendanceEntityToSave)).thenReturn(savedAttendanceEntity);
        when(attendanceMapper.toDto(savedAttendanceEntity)).thenReturn(savedAttendanceDto);

        var result = attendanceService.createAttendance(attendanceDto).booking().bookingStatus();

        assertEquals(BookingStatus.NO_SHOW, result);

    }

    @Test
    void shouldCreateAttendanceAndUpdatePackage_noExceptionThrown() {
        var startDate = LocalDateTime.of(2026, Month.AUGUST, 10, 16, 0);
        var endDate = LocalDateTime.of(2026, Month.AUGUST, 10, 18, 0);
        var validFrom = LocalDate.of(2026, Month.JANUARY, 1);
        var validTo = LocalDate.of(2026, Month.DECEMBER, 31);
        var relatedPlayerEntity = new PlayerEntity(
                1L, "N", null, Handedness.RIGHT, null,
                null, null, null, null, null, null);
        var relatedPlayerDto = new PlayerDto(
                1L, null, null, null,
                null, null, null, null);

        var relatedPackageEntity = new PackageEntity(
                1L, relatedPlayerEntity, "M", 5,
                validFrom, validTo, null, null);
        var relatedTrainingSlotDto = new TrainingSlotDto(
                1L, null, null, startDate, endDate,
                4, SlotStatus.OPEN, null, null, null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(
                1L, null, null, null, startDate, endDate,
                4, SlotStatus.OPEN, null, null, null, null);
        var bookingDto = new BookingDto(
                null, relatedTrainingSlotDto, relatedPlayerDto, BookingStatus.CONFIRMED, null);
        var bookingEntity = new BookingEntity(
                1L, relatedTrainingSlotEntity, relatedPlayerEntity,
                null, BookingStatus.CONFIRMED, null);
        var newBookingEntity = new BookingEntity(
                1L, relatedTrainingSlotEntity, relatedPlayerEntity,
                null, BookingStatus.NO_SHOW, null);
        var newBookingDto = new BookingDto(
                null, relatedTrainingSlotDto, relatedPlayerDto, BookingStatus.NO_SHOW, null);
        var attendanceDto = new AttendanceDto(
                null, bookingDto, Boolean.FALSE, null, null);
        var attendanceEntityToSave = new AttendanceEntity(
                null, newBookingEntity, Boolean.FALSE, null, null);
        var savedAttendanceEntity = new AttendanceEntity(
                1L, newBookingEntity, Boolean.FALSE, null, null);
        var savedAttendanceDto = new AttendanceDto(
                1L, newBookingDto, Boolean.FALSE, null, null);
        relatedPlayerEntity.setPackagee(relatedPackageEntity);

        when(attendanceMapper.toEntity(attendanceDto)).thenReturn(attendanceEntityToSave);
        when(bookingRepository.findById(attendanceDto.booking().id())).thenReturn(Optional.of(bookingEntity));
        when(attendanceRepository.save(attendanceEntityToSave)).thenReturn(savedAttendanceEntity);
        when(attendanceMapper.toDto(savedAttendanceEntity)).thenReturn(savedAttendanceDto);


        var result = attendanceService.createAttendance(attendanceDto);
        assertEquals(savedAttendanceDto, result);
        verify(attendanceMapper).toEntity(attendanceDto);
        verify(attendanceMapper).toDto(savedAttendanceEntity);
        verify(bookingRepository).findById(attendanceDto.booking().id());

    }


    @Test
    void shouldReturnAttendanceDto() {
        var id = 1L;
        var attendanceDto = new AttendanceDto(null, null, Boolean.FALSE, null, null);
        var attendanceEntity = new AttendanceEntity(null, null, Boolean.FALSE, null, null);

        when(attendanceMapper.toDto(attendanceEntity)).thenReturn(attendanceDto);
        when(attendanceRepository.findById(id)).thenReturn(Optional.of(attendanceEntity));

        var result = attendanceService.getAttendance(id);

        assertEquals(attendanceDto, result);

        verify(attendanceMapper).toDto(attendanceEntity);
        verify(attendanceRepository).findById(id);


    }

    @Test
    void shouldThrowEntityNotFoundException_whenAttendanceEntityNotFound() {
        var id = 99L;

        var exception = assertThrows(EntityNotFoundException.class, () -> attendanceService.getAttendance(id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("attendance", id), exception.getMessage());
        verifyNoMoreInteractions(attendanceMapper);

    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnAllAttendancesWithNoFilter() {
        var attendanceDto1 = new AttendanceDto(1L, null, Boolean.TRUE, "A", null);
        var attendanceDto2 = new AttendanceDto(2L, null, Boolean.TRUE, "B", null);
        var attendanceDto3 = new AttendanceDto(3L, null, Boolean.TRUE, "C", null);
        var attendanceEntity1 = new AttendanceEntity(1L, null, Boolean.TRUE, "A", null);
        var attendanceEntity2 = new AttendanceEntity(2L, null, Boolean.TRUE, "B", null);
        var attendanceEntity3 = new AttendanceEntity(3L, null, Boolean.TRUE, "C", null);

        var listOfAttendanceEntities = new ArrayList<AttendanceEntity>();
        listOfAttendanceEntities.add(attendanceEntity1);
        listOfAttendanceEntities.add(attendanceEntity2);
        listOfAttendanceEntities.add(attendanceEntity3);

        var listOfAttendanceDtos = new ArrayList<AttendanceDto>();
        listOfAttendanceDtos.add(attendanceDto1);
        listOfAttendanceDtos.add(attendanceDto2);
        listOfAttendanceDtos.add(attendanceDto3);


        when(attendanceRepository.findAll(any(Specification.class))).thenReturn(listOfAttendanceEntities);
        when(attendanceMapper.toDto(attendanceEntity1)).thenReturn(attendanceDto1);
        when(attendanceMapper.toDto(attendanceEntity2)).thenReturn(attendanceDto2);
        when(attendanceMapper.toDto(attendanceEntity3)).thenReturn(attendanceDto3);

        assertEquals(listOfAttendanceDtos, attendanceService.getAllAttendances(any(AttendanceFilter.class)));
        verify(attendanceMapper).toDto(attendanceEntity1);
        verify(attendanceRepository).findAll(any(Specification.class));

    }


    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowEmptyListException() {
        var emptyList = new ArrayList<AttendanceEntity>();

        when(attendanceRepository.findAll(any(Specification.class))).thenReturn(emptyList);
        var filter = any(AttendanceFilter.class);

        var exception = assertThrows(EmptyListException.class, () -> attendanceService.getAllAttendances(filter));
        assertInstanceOf(EmptyListException.class, exception);
        assertEquals(emptyListMessage("attendance"), exception.getMessage());


    }


    @Test
    void shouldNotThrowExceptionDuringEditing() {
        var id = 1L;
        var bookingEntity = new BookingEntity(
                1L, null,
                null, null, BookingStatus.CONFIRMED, null);
        var bookingDto = new BookingDto(1L, null, null, BookingStatus.CONFIRMED, null);
        var attendanceDto = new AttendanceDto(1L, bookingDto, Boolean.TRUE, "A", null);
        var attendanceEntity = new AttendanceEntity(1L, bookingEntity, Boolean.TRUE, "A", null);


        when(attendanceRepository.findById(id)).thenReturn(Optional.of(attendanceEntity));
        when(bookingRepository.findById(attendanceDto.booking().id())).thenReturn(Optional.of(bookingEntity));

        assertDoesNotThrow(() -> attendanceService.editAttendance(attendanceDto, id));
        verify(attendanceRepository).findById(id);
        verify(bookingRepository).findById(attendanceDto.booking().id());
        verify(attendanceMapper).updateEntity(attendanceEntity, attendanceDto);


    }

    @Test
    void shouldThrowEntityNotFoundException_whenAttendanceNotFound() {
        var id = 99L;
        var attendanceDto = any(AttendanceDto.class);

        var exception = assertThrows(EntityNotFoundException.class, () -> attendanceService.editAttendance(attendanceDto, id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("attendance", id), exception.getMessage());

        verify(attendanceRepository).findById(id);
        verifyNoInteractions(attendanceMapper);
        verifyNoMoreInteractions(attendanceRepository);


    }

    @Test
    void shouldThrowEntityNotFoundException_whenBookingNotFound() {
        var id = 1L;
        var bookingEntity = new BookingEntity(
                1L, null, null, null, BookingStatus.CONFIRMED, null);
        var bookingDto = new BookingDto(1L, null, null, BookingStatus.CONFIRMED, null);
        var attendanceDto = new AttendanceDto(1L, bookingDto, Boolean.TRUE, "A", null);
        var attendanceEntity = new AttendanceEntity(1L, bookingEntity, Boolean.TRUE, "A", null);

        when(attendanceRepository.findById(id)).thenReturn(Optional.of(attendanceEntity));

        var exception = assertThrows(
                EntityNotFoundException.class,
                () -> attendanceService.editAttendance(attendanceDto, id));
        assertEquals(entityNotFoundExceptionMessage(
                "booking", attendanceDto.booking().id()), exception.getMessage());


        assertInstanceOf(EntityNotFoundException.class, exception);
        verify(attendanceRepository).findById(id);
        verify(bookingRepository).findById(attendanceDto.booking().id());
        verify(attendanceMapper).updateEntity(attendanceEntity, attendanceDto);

    }

    @Test
    void shouldNotThrowExceptionDuringDeletion() {
        var id = 1L;

        when(attendanceRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> attendanceService.deleteAttendance(1L));

        verify(attendanceRepository).existsById(id);
        verify(attendanceRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowEntityNotFoundException_WhenAttendanceNotFound() {
        var id = 99L;

        when(attendanceRepository.existsById(id)).thenReturn(false);

        var exception = assertThrows(EntityNotFoundException.class, () -> attendanceService.deleteAttendance(id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("attendance", id), exception.getMessage());

        verify(attendanceRepository).existsById(id);
        verifyNoMoreInteractions(attendanceRepository);


    }


}
