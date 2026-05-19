package cz.reservation.service;

import cz.reservation.constant.Surface;
import cz.reservation.dto.CourtDto;
import cz.reservation.dto.VenueDto;
import cz.reservation.dto.mapper.CourtMapper;
import cz.reservation.entity.CourtEntity;
import cz.reservation.entity.VenueEntity;
import cz.reservation.entity.filter.CourtFilter;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.VenueRepository;
import cz.reservation.service.files.MyFilesUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Mock
    MyFilesUtils filesUtils;

    @Test
    void shouldCreateCourtAndReturnDto() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "file",
                "image/jpeg",
                new byte[1024]);
        var venue = new VenueEntity(
                1L,
                "NAME",
                "ADDRESS",
                "123456789",
                null,
                "url");
        var venueDto = new VenueDto(
                1L,
                "NAME",
                "ADDRESS",
                "123456789",
                "url");
        var courtDtoToSave = new CourtDto(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                venueDto,
                "url");
        var courtEntityToSave = new CourtEntity(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                venue,
                "url");
        var savedCourtEntity = new CourtEntity(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                venue,
                "url");
        var returnedCourtDto = new CourtDto(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                venueDto,
                "url");

        when(courtMapper.toEntity(courtDtoToSave)).thenReturn(courtEntityToSave);
        when(venueRepository.findById(venueDto.id())).thenReturn(Optional.of(venue));
        when(courtRepository.save(courtEntityToSave)).thenReturn(savedCourtEntity);
        when(courtMapper.toDto(savedCourtEntity)).thenReturn(returnedCourtDto);

        var result = courtService.createCourt(courtDtoToSave, file);

        assertEquals(returnedCourtDto, result);
        verify(courtRepository).save(courtEntityToSave);
    }

    @Test
    void shouldReturnCourtDto() {
        var id = 1L;
        var courtEntity = new CourtEntity(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                null,
                "url");
        var courtDto = new CourtDto(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                "url");

        when(courtRepository.findById(id)).thenReturn(Optional.of(courtEntity));
        when(courtMapper.toDto(courtEntity)).thenReturn(courtDto);

        var result = courtService.getCourt(id);

        assertEquals(courtDto, result);
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
    @SuppressWarnings("unchecked")
    void shouldReturnAllCourts() {
        var courtFilter = new CourtFilter(null, null, null, null, null);
        var court1dto = new CourtDto(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                "url");
        var court2dto = new CourtDto(
                2L,
                "M",
                Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                null,
                "url");
        var court1Entity = new CourtEntity(
                1L,
                "N",
                Surface.CLAY,
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                null,
                "url");
        var court2Entity = new CourtEntity(
                2L,
                "M",
                Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                null,
                null,
                "url");

        when(courtRepository.findAll(any(Specification.class))).thenReturn(List.of(court1Entity, court2Entity));
        when(courtMapper.toDto(court1Entity)).thenReturn(court1dto);
        when(courtMapper.toDto(court2Entity)).thenReturn(court2dto);

        var result = courtService.getAllCourts(courtFilter);

        assertEquals(List.of(court1dto, court2dto), result);
        verify(courtRepository).findAll(any(Specification.class));
    }

    @Test
    void shouldDeleteCourtSuccessfully() {
        var id = 1L;

        when(courtRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> courtService.deleteCourt(id));

        verify(courtRepository).existsById(id);
        verify(courtRepository).deleteById(id);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhileDeletingCourt() {
        var id = 99L;
        var exception = assertThrows(EntityNotFoundException.class, () -> courtService.deleteCourt(id));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals("Court with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldEditCourtSuccessfully() {
        var id = 1L;
        var multipartFile = new MockMultipartFile(
                "file",
                "file",
                "image/jpeg",
                new byte[1024]);
        var venue = new VenueEntity(
                1L,
                "NAME",
                "ADDRESS",
                "123456789",
                null,
                "url");
        var venueDto = new VenueDto(
                1L,
                "NAME",
                "ADDRESS",
                "123456789",
                "url");
        var dtoToSave = new CourtDto(
                1L,
                "N",
                Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                venueDto,
                "url");
        var courtEntity = new CourtEntity(
                1L,
                "N",
                Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                null,
                venue,
                "url");

        when(courtRepository.findById(id)).thenReturn(Optional.of(courtEntity));
        when(venueRepository.findById(venueDto.id())).thenReturn(Optional.of(venue));

        assertDoesNotThrow(() -> courtService.editCourt(dtoToSave, id, multipartFile));

        verify(courtRepository).findById(id);
        verify(courtMapper).updateEntity(courtEntity, dtoToSave);
    }

    @Test
    void shouldThrowEntityNotFoundWhileEditing() {
        var multipartFile = new MockMultipartFile("file", new byte[1024]);
        var id = 99L;
        var dtoToSave = new CourtDto(
                id,
                "N",
                Surface.HARD,
                Boolean.TRUE,
                Boolean.TRUE,
                null,
                "url");
        var exception = assertThrows(
                EntityNotFoundException.class,
                () -> courtService.editCourt(dtoToSave, id, multipartFile));

        assertInstanceOf(EntityNotFoundException.class, exception);
        assertEquals(entityNotFoundExceptionMessage("court", id), exception.getMessage());
    }
}
