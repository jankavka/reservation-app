package cz.reservation.service;

import cz.reservation.constant.BookingStatus;
import cz.reservation.constant.EnrollmentState;
import cz.reservation.constant.Handedness;
import cz.reservation.dto.BookingDto;
import cz.reservation.dto.GroupDto;
import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.dto.mapper.BookingMapper;
import cz.reservation.entity.*;
import cz.reservation.entity.filter.BookingFilter;
import cz.reservation.entity.repository.BookingRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.service.exception.EnrollmentNoActiveException;
import cz.reservation.service.exception.LateBookingCancelingException;
import cz.reservation.service.exception.TrainingAlreadyStartedException;
import cz.reservation.service.serviceinterface.TrainingSlotService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TrainingSlotService trainingSlotService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    BookingServiceImpl bookingService;


    @Test
    void shouldCreateAndReturnBookingDto() {
        var startDate = LocalDateTime.now().plusMonths(1);
        var endDate = startDate.plusHours(2);
        List<EnrollmentEntity> enrollmentEntities = List.of(
                new EnrollmentEntity(
                        1L,
                        null,
                        null,
                        null,
                        null),
                new EnrollmentEntity(
                        2L,
                        null,
                        null,
                        null,
                        null),
                new EnrollmentEntity(
                        3L,
                        null,
                        null,
                        null,
                        null)
        );
        var relatedGroupDto = new GroupDto(
                1L,
                "G",
                null,
                null,
                4,
                null);
        var relatedGroupEntity = new GroupEntity(
                1L,
                "G",
                null,
                null,
                enrollmentEntities,
                null);
        var relatedTrainingSlotDto = new TrainingSlotDto(
                1L,
                relatedGroupDto,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(
                1L,
                relatedGroupEntity,
                null,
                null,
                startDate,
                endDate,
                4,
                null,
                null,
                null,
                null,
                null);
        var relatedPlayerDto = new PlayerDto(
                1L,
                "N",
                null,
                Handedness.RIGHT,
                null,
                null,
                null,
                null);
        var relatedPlayerEntity = new PlayerEntity(
                1L,
                "N",
                null,
                Handedness.RIGHT,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        var bookingDtoToSave = new BookingDto(
                null,
                relatedTrainingSlotDto,
                relatedPlayerDto,
                null,
                null);
        var bookingEntityToSave = new BookingEntity(
                null,
                relatedTrainingSlotEntity,
                relatedPlayerEntity,
                null,
                null,
                null);
        var savedEntity = new BookingEntity(
                1L,
                relatedTrainingSlotEntity,
                relatedPlayerEntity,
                null,
                null,
                null);
        var savedDto = new BookingDto(
                1L,
                relatedTrainingSlotDto,
                relatedPlayerDto,
                null,
                null);

        for (EnrollmentEntity e : enrollmentEntities) {
            e.setGroup(relatedGroupEntity);
            e.setPlayer(relatedPlayerEntity);
            e.setState(EnrollmentState.ACTIVE);
        }

        when(bookingMapper.toEntity(bookingDtoToSave)).thenReturn(bookingEntityToSave);
        when(trainingSlotService.getTrainingSlotEntity(relatedTrainingSlotDto.id())).thenReturn(relatedTrainingSlotEntity);
        when(playerRepository.findById(relatedPlayerDto.id())).thenReturn(Optional.of(relatedPlayerEntity));
        when(bookingRepository.save(bookingEntityToSave)).thenReturn(savedEntity);
        when(bookingMapper.toDto(savedEntity)).thenReturn(savedDto);

        var result = bookingService.createBooking(bookingDtoToSave);

        assertEquals(result, savedDto);

        verify(bookingMapper).toEntity(bookingDtoToSave);
        verify(playerRepository).findById(relatedPlayerDto.id());
        verify(trainingSlotService).getTrainingSlotEntity(relatedTrainingSlotDto.id());
        verify(trainingSlotService).getUsedCapacityOfRelatedTrainingSlot(anyLong());
        verifyNoMoreInteractions(trainingSlotService);
        verifyNoMoreInteractions(playerRepository);
    }


    @Test
    void shouldThrowEntityNotFindException() {
        var startDate = LocalDateTime.now().plusMonths(1);
        var endDate = startDate.plusHours(2);
        List<EnrollmentEntity> enrollmentEntities = List.of(
                new EnrollmentEntity(
                        1L,
                        null,
                        null,
                        null,
                        null),
                new EnrollmentEntity(
                        2L,
                        null,
                        null,
                        null,
                        null),
                new EnrollmentEntity(
                        3L,
                        null,
                        null,
                        null,
                        null)
        );
        var relatedGroupDto = new GroupDto(
                1L,
                "G",
                null,
                null,
                4,
                null);
        var relatedGroupEntity = new GroupEntity(
                1L,
                "G",
                null,
                null,
                enrollmentEntities,
                null);
        var relatedTrainingSlotDto = new TrainingSlotDto(
                1L,
                relatedGroupDto,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(
                1L,
                relatedGroupEntity,
                null,
                null,
                startDate,
                endDate,
                4,
                null,
                null,
                null,
                null,
                null);
        var relatedPlayerDto = new PlayerDto(
                1L,
                "N",
                null,
                Handedness.RIGHT,
                null,
                null,
                null,
                null);
        var relatedPlayerEntity = new PlayerEntity(
                1L,
                "N",
                null,
                Handedness.RIGHT,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        var bookingDtoToSave = new BookingDto(
                null,
                relatedTrainingSlotDto,
                relatedPlayerDto,
                null,
                null);
        var bookingEntityToSave = new BookingEntity(
                null,
                relatedTrainingSlotEntity,
                relatedPlayerEntity,
                null,
                null,
                null);

        for (EnrollmentEntity e : enrollmentEntities) {
            e.setGroup(relatedGroupEntity);
            e.setPlayer(relatedPlayerEntity);
            e.setState(EnrollmentState.ACTIVE);
        }


        when(bookingMapper.toEntity(bookingDtoToSave)).thenReturn(bookingEntityToSave);
        when(trainingSlotService.getTrainingSlotEntity(relatedTrainingSlotDto.id()))
                .thenReturn(relatedTrainingSlotEntity);

        var exception = assertThrows(
                EntityNotFoundException.class, () -> bookingService.createBooking(bookingDtoToSave));

        assertInstanceOf(EntityNotFoundException.class, exception);
        verifyNoMoreInteractions(bookingMapper);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void shouldThrowEnrollmentNoActiveException() {
        var startDate = LocalDateTime.now().plusMonths(1);
        var endDate = startDate.plusHours(2);
        List<EnrollmentEntity> enrollmentEntities = List.of(
                new EnrollmentEntity(
                        1L,
                        null,
                        null,
                        null,
                        null),
                new EnrollmentEntity(
                        2L,
                        null,
                        null,
                        null,
                        null),
                new EnrollmentEntity(
                        3L,
                        null,
                        null,
                        null,
                        null)
        );
        var relatedGroupDto = new GroupDto(
                1L,
                "G",
                null,
                null,
                4,
                null);
        var relatedGroupEntity = new GroupEntity(
                1L,
                "G",
                null,
                null,
                enrollmentEntities,
                null);
        var relatedTrainingSlotDto = new TrainingSlotDto(
                1L,
                relatedGroupDto,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(
                1L,
                relatedGroupEntity,
                null,
                null,
                startDate,
                endDate,
                4,
                null,
                null,
                null,
                null,
                null);
        var relatedPlayerDto = new PlayerDto(
                1L,
                "N",
                null,
                Handedness.RIGHT,
                null,
                null,
                null,
                null);
        var relatedPlayerEntity = new PlayerEntity(
                1L,
                "N",
                null,
                Handedness.RIGHT,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        var bookingDtoToSave = new BookingDto(
                null,
                relatedTrainingSlotDto,
                relatedPlayerDto,
                null,
                null);
        var bookingEntityToSave = new BookingEntity(
                null,
                relatedTrainingSlotEntity,
                relatedPlayerEntity,
                null,
                null,
                null);

        for (EnrollmentEntity e : enrollmentEntities) {
            e.setGroup(relatedGroupEntity);
            e.setPlayer(relatedPlayerEntity);
            e.setState(EnrollmentState.WAITLIST);
        }

        when(bookingMapper.toEntity(bookingDtoToSave)).thenReturn(bookingEntityToSave);
        when(trainingSlotService.getTrainingSlotEntity(relatedTrainingSlotDto.id())).thenReturn(relatedTrainingSlotEntity);
        when(playerRepository.findById(relatedPlayerDto.id())).thenReturn(Optional.of(relatedPlayerEntity));


        var exception = assertThrows(EnrollmentNoActiveException.class, () -> bookingService.createBooking(bookingDtoToSave));
        assertInstanceOf(EnrollmentNoActiveException.class, exception);

        verifyNoMoreInteractions(playerRepository);
        verifyNoInteractions(bookingRepository);

    }

    @Test
    void shouldThrowTrainingAlreadyStarted() {
        var startDate = LocalDateTime.now().minusHours(2);
        var endDate = startDate.plusHours(2);
        List<EnrollmentEntity> enrollmentEntities = List.of(
                new EnrollmentEntity(
                        1L,
                        null,
                        null,
                        null,
                        null),
                new EnrollmentEntity(
                        2L,
                        null,
                        null,
                        null,
                        null),
                new EnrollmentEntity(
                        3L,
                        null,
                        null,
                        null,
                        null)
        );
        var relatedGroupDto = new GroupDto(
                1L,
                "G",
                null,
                null,
                4,
                null);
        var relatedGroupEntity = new GroupEntity(
                1L,
                "G",
                null,
                null,
                enrollmentEntities,
                null);
        var relatedTrainingSlotDto = new TrainingSlotDto(
                1L,
                relatedGroupDto,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(
                1L,
                relatedGroupEntity,
                null,
                null,
                startDate,
                endDate,
                4,
                null,
                null,
                null,
                null,
                null);
        var relatedPlayerDto = new PlayerDto(
                1L,
                "N",
                null,
                Handedness.RIGHT,
                null,
                null,
                null,
                null);
        var relatedPlayerEntity = new PlayerEntity(
                1L,
                "N",
                null,
                Handedness.RIGHT,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        var bookingDtoToSave = new BookingDto(
                null,
                relatedTrainingSlotDto,
                relatedPlayerDto,
                null,
                null);
        var bookingEntityToSave = new BookingEntity(
                null,
                relatedTrainingSlotEntity,
                relatedPlayerEntity,
                null,
                null,
                null);

        for (EnrollmentEntity e : enrollmentEntities) {
            e.setGroup(relatedGroupEntity);
            e.setPlayer(relatedPlayerEntity);
            e.setState(EnrollmentState.ACTIVE);
        }

        when(bookingMapper.toEntity(bookingDtoToSave)).thenReturn(bookingEntityToSave);
        when(trainingSlotService.getTrainingSlotEntity(relatedTrainingSlotDto.id())).thenReturn(relatedTrainingSlotEntity);
        when(playerRepository.findById(relatedPlayerDto.id())).thenReturn(Optional.of(relatedPlayerEntity));

        var exception = assertThrows(TrainingAlreadyStartedException.class, () -> bookingService.createBooking(bookingDtoToSave));
        assertInstanceOf(TrainingAlreadyStartedException.class, exception);

        verifyNoInteractions(bookingRepository);
        verifyNoMoreInteractions(playerRepository);
        verifyNoMoreInteractions(trainingSlotService);


    }

    @Test
    void shouldReturnBookingDto() {
        var id = 1L;
        var bookingDto = new BookingDto(1L, null, null, null, null);
        var bookingEntity = new BookingEntity(1L, null, null, null, null, null);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntity));
        when(bookingMapper.toDto(bookingEntity)).thenReturn(bookingDto);

        var result = bookingService.getBooking(1L);
        assertEquals(bookingDto, result);

        verify(bookingRepository).findById(1L);
        verify(bookingMapper).toDto(bookingEntity);
    }

    @Test
    void shouldThrowEntityNotFoundException_InCaseBookingMissing() {
        var id = 99L;

        var exception = assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(id));
        assertInstanceOf(EntityNotFoundException.class, exception);
    }


    @Test
    void shouldEditBookingSuccessfully() {
        var id = 1L;
        var startDate = LocalDateTime.now().plusHours(26);
        var endDate = startDate.plusHours(2);
        var relatedTrainingSlotDto = new TrainingSlotDto(1L, null, null, startDate, endDate, null, null, null, null, null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(1L, null, null, null, startDate, endDate, null, null, null, null, null, null);
        var bookingDtoToEdit = new BookingDto(1L, relatedTrainingSlotDto, null, BookingStatus.CONFIRMED, null);
        var bookingEntityToUpdate = new BookingEntity(1L, relatedTrainingSlotEntity, null, null, BookingStatus.CONFIRMED, null);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntityToUpdate));
        when(trainingSlotService.getTrainingSlotEntity(bookingDtoToEdit.trainingSlot().id())).thenReturn(relatedTrainingSlotEntity);

        assertDoesNotThrow(() -> bookingService.editBooking(bookingDtoToEdit, id));

        verify(bookingRepository).findById(id);
        verify(trainingSlotService).getTrainingSlotEntity(id);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(trainingSlotService);

    }

    @Test
    void shouldThrowEntityNotFoundException_inCaseNoBookingFound() {
        var id = 99L;
        var bookingDto = new BookingDto(1L, null, null, null, null);

        var exception = assertThrows(EntityNotFoundException.class, () -> bookingService.editBooking(bookingDto, id));
        assertInstanceOf(EntityNotFoundException.class, exception);
    }

    @Test
    void shouldThrowLateBookingCancelingException() {
        var id = 1L;
        var startDate = LocalDateTime.now().plusHours(23);
        var endDate = startDate.plusHours(2);
        var relatedTrainingSlotDto = new TrainingSlotDto(1L, null, null, startDate, endDate, null, null, null, null, null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(1L, null, null, null, startDate, endDate, null, null, null, null, null, null);
        var bookingDtoToEdit = new BookingDto(1L, relatedTrainingSlotDto, null, BookingStatus.CANCELED, null);
        var bookingEntityToUpdate = new BookingEntity(1L, relatedTrainingSlotEntity, null, null, BookingStatus.CANCELED, null);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntityToUpdate));
        when(trainingSlotService.getTrainingSlotEntity(bookingEntityToUpdate.getTrainingSlot().getId())).thenReturn(relatedTrainingSlotEntity);


        assertThrows(LateBookingCancelingException.class, () -> bookingService.editBooking(bookingDtoToEdit, id));
        verify(bookingRepository).findById(id);
        verify(trainingSlotService).getTrainingSlotEntity(bookingEntityToUpdate.getTrainingSlot().getId());
        verifyNoInteractions(playerRepository);
        verifyNoInteractions(bookingMapper);

    }

    @Test
    void shouldNotThrowException_whileEditingByAdmin() {
        var id = 1L;
        var startDate = LocalDateTime.now().plusHours(23);
        var endDate = startDate.plusHours(2);
        var relatedPlayerDto = new PlayerDto(1L, "N", null, Handedness.RIGHT, null, null, null, null);
        var relatedPlayerEntity = new PlayerEntity(1L, "N", null, Handedness.RIGHT, null, null, null, null, null, null, null);
        var relatedTrainingSlotDto = new TrainingSlotDto(1L, null, null, startDate, endDate, null, null, null, null, null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(1L, null, null, null, startDate, endDate, null, null, null, null, null, null);
        var bookingDtoToEdit = new BookingDto(1L, relatedTrainingSlotDto, relatedPlayerDto, BookingStatus.CANCELED, null);
        var bookingEntityToUpdate = new BookingEntity(1L, relatedTrainingSlotEntity, relatedPlayerEntity, null, BookingStatus.CANCELED, null);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntityToUpdate));
        when(trainingSlotService.getTrainingSlotEntity(bookingDtoToEdit.trainingSlot().id())).thenReturn(relatedTrainingSlotEntity);
        when(playerRepository.findById(bookingDtoToEdit.player().id())).thenReturn(Optional.of(relatedPlayerEntity));

        assertDoesNotThrow(() -> bookingService.editBookingAsAdmin(bookingDtoToEdit, id));

        verify(bookingRepository).findById(id);
        verify(trainingSlotService).getTrainingSlotEntity(bookingDtoToEdit.trainingSlot().id());
        verify(playerRepository).findById(bookingDtoToEdit.player().id());
        verify(bookingMapper).updateEntity(bookingEntityToUpdate, bookingDtoToEdit);
        verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void shouldThrowEntityNotFoundException_whileFindingBookingAsAdmin() {
        var id = 1L;
        var bookingDtoToEdit = new BookingDto(null, null, null, BookingStatus.CANCELED, null);

        var exception = assertThrows(
                EntityNotFoundException.class, () -> bookingService.editBookingAsAdmin(bookingDtoToEdit, id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("booking", id), exception.getMessage());

        verify(bookingRepository).findById(id);
        verifyNoInteractions(trainingSlotService);
        verifyNoInteractions(playerRepository);
        verifyNoInteractions(bookingMapper);

    }

    @Test
    void shouldThrowEntityNotFoundException_whileFindingTrainingSlotAsAdmin() {
        var id = 1L;
        var startDate = LocalDateTime.now().plusHours(23);
        var endDate = startDate.plusHours(2);
        var relatedPlayerDto = new PlayerDto(1L, "N", null, Handedness.RIGHT, null, null, null, null);
        var relatedPlayerEntity = new PlayerEntity(1L, "N", null, Handedness.RIGHT, null, null, null, null, null, null, null);
        var relatedTrainingSlotDto = new TrainingSlotDto(1L, null, null, startDate, endDate, null, null, null, null, null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(1L, null, null, null, startDate, endDate, null, null, null, null, null, null);
        var bookingDtoToEdit = new BookingDto(1L, relatedTrainingSlotDto, relatedPlayerDto, BookingStatus.CANCELED, null);
        var bookingEntityToUpdate = new BookingEntity(1L, relatedTrainingSlotEntity, relatedPlayerEntity, null, BookingStatus.CANCELED, null);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntityToUpdate));
        when(trainingSlotService.getTrainingSlotEntity(bookingDtoToEdit.trainingSlot().id())).thenThrow(new EntityNotFoundException(entityNotFoundExceptionMessage("training slot", bookingDtoToEdit.trainingSlot().id())));

        var exception = assertThrows(
                EntityNotFoundException.class, () -> bookingService.editBookingAsAdmin(bookingDtoToEdit, id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage(
                "training slot", bookingDtoToEdit.trainingSlot().id()), exception.getMessage());

        verify(bookingRepository).findById(id);

        verifyNoInteractions(playerRepository);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void shouldThrowEntityNotFoundException_whileFindingPlayerAsAdmin() {
        var id = 1L;
        var startDate = LocalDateTime.now().plusHours(23);
        var endDate = startDate.plusHours(2);
        var relatedPlayerDto = new PlayerDto(1L, "N", null, Handedness.RIGHT, null, null, null, null);
        var relatedPlayerEntity = new PlayerEntity(1L, "N", null, Handedness.RIGHT, null, null, null, null, null, null, null);
        var relatedTrainingSlotDto = new TrainingSlotDto(1L, null, null, startDate, endDate, null, null, null, null, null);
        var relatedTrainingSlotEntity = new TrainingSlotEntity(1L, null, null, null, startDate, endDate, null, null, null, null, null, null);
        var bookingDtoToEdit = new BookingDto(1L, relatedTrainingSlotDto, relatedPlayerDto, BookingStatus.CANCELED, null);
        var bookingEntityToUpdate = new BookingEntity(1L, relatedTrainingSlotEntity, relatedPlayerEntity, null, BookingStatus.CANCELED, null);


        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntityToUpdate));
        when(trainingSlotService.getTrainingSlotEntity(bookingDtoToEdit.trainingSlot().id())).thenReturn(relatedTrainingSlotEntity);

        var exception = assertThrows(EntityNotFoundException.class, () -> bookingService.editBookingAsAdmin(bookingDtoToEdit, id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("player", bookingDtoToEdit.player().id()), exception.getMessage());

        verify(bookingRepository).findById(id);
        verify(trainingSlotService).getTrainingSlotEntity(bookingDtoToEdit.trainingSlot().id());
        verifyNoInteractions(bookingMapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnListOfAllBookings() {
        var bookingDto1 = new BookingDto(1L, null, null, null, null);
        var bookingDto2 = new BookingDto(2L, null, null, null, null);
        var bookingDto3 = new BookingDto(3L, null, null, null, null);
        var bookingEntity1 = new BookingEntity(1L, null, null, null, null, null);
        var bookingEntity2 = new BookingEntity(2L, null, null, null, null, null);
        var bookingEntity3 = new BookingEntity(3L, null, null, null, null, null);
        List<BookingEntity> entities = List.of(bookingEntity1, bookingEntity2, bookingEntity3);
        List<BookingDto> bookingDtos = List.of(bookingDto1, bookingDto2, bookingDto3);


        when(bookingRepository.findAll(any(Specification.class))).thenReturn(entities);
        when(bookingMapper.toDto(bookingEntity1)).thenReturn(bookingDto1);
        when(bookingMapper.toDto(bookingEntity2)).thenReturn(bookingDto2);
        when(bookingMapper.toDto(bookingEntity3)).thenReturn(bookingDto3);

        var result = bookingService.getAllBookings(any(BookingFilter.class));

        assertEquals(bookingDtos, result);

        verify(bookingRepository).findAll(any(Specification.class));
        verify(bookingMapper, times(3)).toDto(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnEmptyListWithNoException() {
        var emptyListOfEntities = new ArrayList<BookingEntity>();
        var emptyListOfDtos = new ArrayList<BookingDto>();

        when(bookingRepository.findAll(any(Specification.class))).thenReturn(emptyListOfEntities);

        var result = bookingService.getAllBookings(any(BookingFilter.class));

        assertEquals(emptyListOfDtos, result);

        verify(bookingRepository).findAll(any(Specification.class));
        verifyNoMoreInteractions(bookingRepository);

    }

    @Test
    void shouldNoThrowExceptionDuringDeletion() {
        var id = 1L;
        var startDate = LocalDateTime.now().plusHours(24).plusMinutes(1);
        var endDate = startDate.plusHours(2);
        var trainingSlotEntity = new TrainingSlotEntity(1L, null, null, null, startDate, endDate, null, null, null, null, null, null);
        var bookingEntity = new BookingEntity(1L, trainingSlotEntity, null, null, null, null);


        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntity));
        when(trainingSlotService.getTrainingSlotEntity(bookingEntity.getTrainingSlot().getId())).thenReturn(trainingSlotEntity);

        assertDoesNotThrow(() -> bookingService.deleteBooking(id));

        verify(bookingRepository).findById(id);
        verify(trainingSlotService).getTrainingSlotEntity(bookingEntity.getTrainingSlot().getId());
        verifyNoMoreInteractions(bookingRepository);


    }


    @Test
    void shouldThrowEntityNotFoundExceptionDuringDeletion_inCaseNoBooking() {
        var exception = assertThrows(EntityNotFoundException.class, () -> bookingService.deleteBooking(99L));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("booking", 99L), exception.getMessage());

        verify(bookingRepository).findById(99L);
        verifyNoInteractions(trainingSlotService);

    }

    @Test
    void shouldThrowEntityNotFoundExceptionDuringDeletion_inCaseNoTrainingSlot() {
        var id = 1L;
        var startDate = LocalDateTime.now().plusHours(24).plusMinutes(1);
        var endDate = startDate.plusHours(2);
        var trainingSlotEntity = new TrainingSlotEntity(99L, null, null, null, startDate, endDate, null, null, null, null, null, null);
        var bookingEntity = new BookingEntity(1L, trainingSlotEntity, null, null, null, null);


        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntity));
        when(trainingSlotService.getTrainingSlotEntity(bookingEntity.getTrainingSlot().getId())).thenThrow(new EntityNotFoundException(entityNotFoundExceptionMessage("training slot", 99L)));

        var exception = assertThrows(EntityNotFoundException.class, () -> bookingService.deleteBooking(1L));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("training slot", 99L), exception.getMessage());

        verify(bookingRepository).findById(1L);
        verify(trainingSlotService).getTrainingSlotEntity(99L);

    }

    @Test
    void shouldThrowLateBookingCancelingException_duringDeletion() {
        var id = 1L;
        var startDate = LocalDateTime.now().plusHours(23);
        var endDate = startDate.plusHours(1);
        var trainingSlotEntity = new TrainingSlotEntity(1L, null, null, null, startDate, endDate, null, null, null, null, null, null);
        var bookingEntity = new BookingEntity(1L, trainingSlotEntity, null, null, null, null);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntity));
        when(trainingSlotService.getTrainingSlotEntity(bookingEntity.getTrainingSlot().getId())).thenReturn(trainingSlotEntity);

        var exception = assertThrows(LateBookingCancelingException.class, () -> bookingService.deleteBooking(id));

        assertInstanceOf(LateBookingCancelingException.class, exception);

        assertEquals("Can not cancel reservation less than 24 hours before training slot", exception.getMessage());

        verify(bookingRepository).findById(id);
        verify(trainingSlotService).getTrainingSlotEntity(bookingEntity.getTrainingSlot().getId());

    }

    @Test
    void shouldReturnListOfBookingsAccordingToParams() {
        var playerId = 1L;
        var month = Month.AUGUST;
        var year = 2026;
        var yearMonth = YearMonth.of(year, month);
        var startDate = yearMonth.atDay(1).atStartOfDay();
        var endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        var bookingDto1 = new BookingDto(1L, null, null, null, null);
        var bookingDto2 = new BookingDto(2L, null, null, null, null);
        var bookingDto3 = new BookingDto(3L, null, null, null, null);
        var bookingEntity1 = new BookingEntity(1L, null, null, null, null, null);
        var bookingEntity2 = new BookingEntity(2L, null, null, null, null, null);
        var bookingEntity3 = new BookingEntity(3L, null, null, null, null, null);
        List<BookingDto> bookingDtos = List.of(bookingDto1, bookingDto2, bookingDto3);

        when(bookingRepository.findByPlayerIdAndTrainingSlotStartAtBetween(playerId, startDate, endDate)).thenReturn(
                List.of(bookingEntity1, bookingEntity2, bookingEntity3));
        when(bookingMapper.toDto(bookingEntity1)).thenReturn(bookingDto1);
        when(bookingMapper.toDto(bookingEntity2)).thenReturn(bookingDto2);
        when(bookingMapper.toDto(bookingEntity3)).thenReturn(bookingDto3);

        var result = bookingService.getBookingsByPlayerIdAndMonth(playerId, month, year);

        assertEquals(bookingDtos, result);
        verify(bookingRepository).findByPlayerIdAndTrainingSlotStartAtBetween(playerId, startDate, endDate);
        verify(bookingMapper, times(3)).toDto(any(BookingEntity.class));
    }

    @Test
    void shouldNotThrowExceptionWhenEmptyListIsReturned() {
        var playerId = 1L;
        var month = Month.AUGUST;
        var year = 2026;
        var yearMonth = YearMonth.of(year, month);
        var startDate = yearMonth.atDay(1).atStartOfDay();
        var endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
        var emptyListOfEntities = new ArrayList<BookingEntity>();
        var emptyListOfDtos = new ArrayList<BookingDto>();

        when(bookingRepository.findByPlayerIdAndTrainingSlotStartAtBetween(playerId, startDate, endDate)).thenReturn(emptyListOfEntities);

        var result = bookingService.getBookingsByPlayerIdAndMonth(playerId, month, year);

        assertEquals(emptyListOfDtos, result);

        verifyNoInteractions(bookingMapper);
        verifyNoMoreInteractions(bookingRepository);

    }


}
