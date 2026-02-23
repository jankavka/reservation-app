package cz.reservation.service;

import cz.reservation.constant.Surface;
import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.dto.CourtDto;
import cz.reservation.dto.mapper.CourtBlockingMapper;
import cz.reservation.entity.CourtBlockingEntity;
import cz.reservation.entity.CourtEntity;
import cz.reservation.entity.repository.CourtBlockingRepository;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.service.exception.UnsupportedTimeRangeException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourtBlockingServiceTest {

    @Mock
    CourtBlockingMapper courtBlockingMapper;

    @Mock
    CourtBlockingRepository courtBlockingRepository;

    @Mock
    CourtRepository courtRepository;

    @InjectMocks
    CourtBlockingServiceImpl service;

    @Test
    void shouldCreateCourtBlocking() {
        var dateFrom = LocalDateTime.of(2026, Month.AUGUST, 12, 12, 0);
        var dateTo = LocalDateTime.of(2026, Month.AUGUST, 12, 13, 0);
        var courtDto = new CourtDto(1L, null, Surface.HARD, Boolean.FALSE, Boolean.TRUE, null);
        var courtEntity = new CourtEntity(
                1L, null, Surface.HARD, Boolean.FALSE, Boolean.TRUE, null, null);
        var courtBlockingDto = new CourtBlockingDto(null, courtDto, dateFrom, dateTo, null, null);
        var courtBlockingEntity = new CourtBlockingEntity();
        courtBlockingEntity.setBlockedFrom(dateFrom);
        courtBlockingEntity.setBlockedTo(dateTo);
        courtBlockingEntity.setCourt(courtEntity);
        var savedCourtBlockingEntity = new CourtBlockingEntity(
                1L, courtEntity, dateFrom, dateTo, null, null, courtBlockingEntity.getRange());
        var savedCourtBlockDto = new CourtBlockingDto(
                1L, courtDto, dateFrom, dateTo, null, savedCourtBlockingEntity.getRange());


        when(courtBlockingMapper.toEntity(courtBlockingDto)).thenReturn(courtBlockingEntity);
        when(courtRepository.findById(courtBlockingDto.court().id())).thenReturn(Optional.of(courtEntity));
        when(courtBlockingRepository.save(courtBlockingEntity)).thenReturn(savedCourtBlockingEntity);
        when(courtBlockingMapper.toDto(savedCourtBlockingEntity)).thenReturn(savedCourtBlockDto);

        var result = service.createBlocking(courtBlockingDto);

        assertEquals(savedCourtBlockDto.blockedFrom(), result.blockedFrom());
        assertEquals(savedCourtBlockDto.id(), result.id());

        verify(courtBlockingMapper).toEntity(courtBlockingDto);
        verify(courtRepository).findById(courtBlockingDto.court().id());
        verify(courtBlockingRepository).save(courtBlockingEntity);
        verify(courtBlockingMapper).toDto(savedCourtBlockingEntity);

    }

    @Test
    void shouldThrowUnsupportedTimeRangeException() {
        var dateFrom = LocalDateTime.of(2026, Month.AUGUST, 12, 12, 1);
        var dateTo = LocalDateTime.of(2026, Month.AUGUST, 12, 13, 1);
        var courtDto = new CourtDto(1L, null, Surface.HARD, Boolean.FALSE, Boolean.TRUE, null);
        var courtBlockingDto = new CourtBlockingDto(null, courtDto, dateFrom, dateTo, null, null);

        var exception = assertThrows(
                UnsupportedTimeRangeException.class,
                () -> service.createBlocking(courtBlockingDto));

        assertInstanceOf(UnsupportedTimeRangeException.class, exception);

        verifyNoInteractions(courtBlockingMapper);
        verifyNoInteractions(courtBlockingRepository);
        verifyNoInteractions(courtRepository);
    }

    @Test
    void shouldThrowEntityNotFoundException_inCaseNoRelatedCourt() {
        var dateFrom = LocalDateTime.of(2026, Month.AUGUST, 12, 12, 0);
        var dateTo = LocalDateTime.of(2026, Month.AUGUST, 12, 13, 0);
        var courtDto = new CourtDto(99L, null, null, null, null, null);
        var courtEntity = new CourtEntity(
                99L, null, null, null, null, null, null);
        var courtBlockingDto = new CourtBlockingDto(null, courtDto, dateFrom, dateTo, null, null);
        var courtBlockingEntity = new CourtBlockingEntity();
        courtBlockingEntity.setBlockedFrom(dateFrom);
        courtBlockingEntity.setBlockedTo(dateTo);
        courtBlockingEntity.setCourt(courtEntity);

        when(courtBlockingMapper.toEntity(courtBlockingDto)).thenReturn(courtBlockingEntity);

        var exception = assertThrows(EntityNotFoundException.class, () -> service.createBlocking(courtBlockingDto));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("court", 99L), exception.getMessage());

        verify(courtBlockingMapper, times(1)).toEntity(courtBlockingDto);
        verify(courtRepository, times(1)).findById(99L);
        verifyNoMoreInteractions(courtBlockingMapper);
        verifyNoInteractions(courtBlockingRepository);

    }

    @Test
    void shouldReturnCourtBlocking() {
        var dateFrom = LocalDateTime.of(2026, Month.AUGUST, 12, 12, 0);
        var dateTo = dateFrom.plusHours(2);
        var id = 1L;
        var courtBlockingEntity = new CourtBlockingEntity(
                id, null, dateFrom, dateTo, null, null, null);
        var courtBlockingDto = new CourtBlockingDto(
                id, null, dateFrom, dateTo, null, null);

        when(courtBlockingRepository.findById(id)).thenReturn(Optional.of(courtBlockingEntity));
        when(courtBlockingMapper.toDto(courtBlockingEntity)).thenReturn(courtBlockingDto);

        var result = service.getBlocking(id);

        assertEquals(courtBlockingDto.id(), result.id());
        assertEquals(courtBlockingDto.blockedFrom(), result.blockedFrom());
        assertEquals(courtBlockingDto.blockedTo(), result.blockedTo());

        verify(courtBlockingRepository, times(1)).findById(id);
        verify(courtBlockingMapper, times(1)).toDto(courtBlockingEntity);

    }

    @Test
    void shouldThrowEntityNotFoundException_whileNoSuchBlocking(){
        var id = 99L;

        var exception = assertThrows(EntityNotFoundException.class,() -> service.getBlocking(id));

        assertInstanceOf(EntityNotFoundException.class, exception);

        assertEquals(entityNotFoundExceptionMessage("blocking", id), exception.getMessage());

        verify(courtBlockingRepository).findById(id);
        verifyNoMoreInteractions(courtBlockingRepository);
        verifyNoInteractions(courtBlockingMapper);
    }
}
