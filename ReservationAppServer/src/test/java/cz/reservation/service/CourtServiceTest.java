package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.Surface;
import cz.reservation.dto.CourtDto;
import cz.reservation.dto.VenueDto;
import cz.reservation.dto.mapper.CourtMapper;
import cz.reservation.entity.CourtEntity;
import cz.reservation.entity.VenueEntity;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.VenueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static cz.reservation.service.message.MessageHandling.*;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @Mock
    CourtRepository courtRepository;

    @Mock
    CourtMapper courtMapper;

    @Mock
    VenueRepository venueRepository;

    @InjectMocks
    CourtServiceImpl courtService;


    @Test
    void shouldReturnResponseEntityWithCourtDto() {
        var venue = new VenueEntity(1L,
                "NAME",
                "ADDRESS",
                "123456789",
                null);
        var venueDto = new VenueDto(
                1L,
                "NAME",
                "ADDRESS",
                "123456789");
        var courtDtoToSave = new CourtDto(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                new VenueDto(
                        1L,
                        "NAME",
                        "ADDRESS",
                        "123456789"));
        var courtEntityToSave = new CourtEntity(
                1L, "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                venue);
        var savedCourtEntity = new CourtEntity(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                venue);
        var returnedCourtDto = new CourtDto(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                venueDto);

        when(courtMapper.toEntity(courtDtoToSave)).thenReturn(courtEntityToSave);
        when(venueRepository.existsById(venue.getId())).thenReturn(true);
        when(venueRepository.getReferenceById(venue.getId())).thenReturn(venue);
        when(courtRepository.save(courtEntityToSave)).thenReturn(savedCourtEntity);
        when(courtMapper.toDto(savedCourtEntity)).thenReturn(returnedCourtDto);

        var result = courtService.createCourt(courtDtoToSave);

        assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(returnedCourtDto), result);
        verify(courtRepository).save(courtEntityToSave);
        verifyNoMoreInteractions(courtRepository);
        verify(venueRepository).getReferenceById(venue.getId());

    }

    @Test
    void shouldGetResponseEntityWithCourtDto() {
        var id = 1L;
        var courtEntity = new CourtEntity(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                null);
        var courtDto = new CourtDto(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null);

        when(courtMapper.toDto(courtEntity)).thenReturn(courtDto);
        when(courtRepository.findById(id)).thenReturn(Optional.of(courtEntity));

        var result = courtService.getCourt(id);

        assertEquals(ResponseEntity.ok(courtDto), result);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhileGettingCourt() {
        var exception = assertThrows(
                EntityNotFoundException.class,
                () -> courtService.getCourt(99L));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("court", 99L), exception.getMessage());

    }

    @Test
    void shouldGetAllCourtDto() {
        var court1dto = new CourtDto(1L, "N", Surface.CLAY, Boolean.FALSE, Boolean.TRUE, null);
        var court2dto = new CourtDto(1L, "M", Surface.HARD, Boolean.TRUE, Boolean.TRUE, null);
        var court1Entity = new CourtEntity(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                null);
        var court2Entity = new CourtEntity(
                2L, "M", Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                null,
                null);

        when(courtRepository.findAll()).thenReturn(List.of(court1Entity, court2Entity));
        when(courtMapper.toDto(court1Entity)).thenReturn(court1dto);
        when(courtMapper.toDto(court2Entity)).thenReturn(court2dto);

        var result = courtService.getAllCourts();

        assertEquals(ResponseEntity.ok(List.of(court1dto, court2dto)), result);
        verify(courtRepository).findAll();
        verifyNoMoreInteractions(courtRepository);

    }

    @Test
    void shouldReturnResponseEntityAndOkMessage() {
        var id = 1L;

        when(courtRepository.existsById(id)).thenReturn(true);

        var result = courtService.deleteCourt(id);

        assertEquals(
                ResponseEntity.ok(Map.of(
                        "message",
                        successMessage("court", id, EventStatus.DELETED))),
                result);

        verify(courtRepository).existsById(id);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhileDeletingCourt() {
        var id = 99L;
        var exception = assertThrows(EntityNotFoundException.class, () -> courtService.deleteCourt(id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals("Court with id " + id + " not found", exception.getMessage());

    }

    @Test
    void shouldReturnResponseEntityWithHttpStatusWhileEditing() {
        var id = 1L;
        var venue = new VenueEntity(1L,
                "NAME",
                "ADDRESS",
                "123456789",
                null);
        var dtoToSave = new CourtDto(
                1L,
                "N",
                Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                new VenueDto(1L,"NAME","ADDRESS","123456789"));
        var courtEntity = new CourtEntity(
                1L,
                "N",
                Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                null,
                venue
        );


        when(courtRepository.existsById(id)).thenReturn(true);
        when(courtRepository.getReferenceById(id)).thenReturn(courtEntity);
        when(courtMapper.toDto(courtEntity)).thenReturn(dtoToSave);

        var result = courtService.editCourt(dtoToSave, id);

        assertEquals(ResponseEntity.ok(Map.of("message", successMessage(
                "court",1L,EventStatus.UPDATED), "object", dtoToSave.toString())), result);
        verify(courtRepository).existsById(id);

    }

    @Test
    void shouldThrowEntityNotFoundWhileEditing() {
        var id = 99L;
        var dtoToSave = new CourtDto(
                id,
                "N",
                Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                null);
        var exception = assertThrows(EntityNotFoundException.class, () -> courtService.editCourt(dtoToSave, id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("court",id), exception.getMessage());
    }



}
