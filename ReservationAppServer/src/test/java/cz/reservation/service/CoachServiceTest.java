package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.Role;
import cz.reservation.dto.CoachDto;
import cz.reservation.dto.UserDto;
import cz.reservation.dto.mapper.CoachMapper;
import cz.reservation.entity.CoachEntity;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.GroupEntity;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static cz.reservation.service.message.MessageHandling.*;

@ExtendWith(MockitoExtension.class)
class CoachServiceTest {

    @Mock
    CoachRepository coachRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GroupRepository groupRepository;

    @Mock
    CoachMapper coachMapper;

    @InjectMocks
    CoachServiceImpl coachService;


    LocalDateTime date = LocalDateTime.of(2025, 8, 10, 0, 0);


    @Test
    void shouldReturnCoachDtoAndStatusOk() {
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        var relatedUserDto = new UserDto(
                1L,
                "example@email.com",
                "12345609097",
                "N",
                roles,
                date);
        var relatedUserEntity = new UserEntity(
                1L,
                "example@email.com",
                "12345609097",
                "123456",
                "N",
                roles,
                null,
                date,
                null,
                null);
        var coachToSaveDto = new CoachDto(
                null, relatedUserDto,
                "B",
                "C");
        var savedCoachDto = new CoachDto(
                1L,
                relatedUserDto,
                "B",
                "C");
        var coachToSaveEntity = new CoachEntity(
                null,
                relatedUserEntity,
                "B",
                "C",
                null);
        var savedCoachEntity = new CoachEntity(
                1L,
                relatedUserEntity,
                "B",
                "C",
                null);

        when(coachMapper.toEntity(coachToSaveDto)).thenReturn(coachToSaveEntity);
        when(userRepository.existsById(relatedUserDto.id())).thenReturn(true);
        when(userRepository.getReferenceById(coachToSaveDto.user().id())).thenReturn(relatedUserEntity);
        when(coachRepository.save(coachToSaveEntity)).thenReturn(savedCoachEntity);
        when(coachMapper.toDto(savedCoachEntity)).thenReturn(savedCoachDto);


        var result = coachService.createCoach(coachToSaveDto);

        assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(savedCoachDto), result);
        verify(coachRepository).save(coachToSaveEntity);

    }

    @Test
    void shouldReturnResponseEntityWithCoachDto() {
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);

        var id = 1L;
        var relatedUserEntity = new UserEntity(
                1L,
                "example@email.com",
                "12345609097",
                "123456",
                "N",
                roles,
                null,
                date,
                null,
                null);
        var relatedUserDto = new UserDto(
                1L,
                "example@email.com",
                "12345609097",
                "N",
                roles,
                date);
        var coachEntity = new CoachEntity(
                1L,
                relatedUserEntity,
                "B",
                "C",
                null);

        var coachDto = new CoachDto(
                1L,
                relatedUserDto,
                "B",
                "C");

        when(coachMapper.toDto(coachEntity)).thenReturn(coachDto);
        when(coachRepository.findById(id)).thenReturn(Optional.of(coachEntity));


        var result = coachService.getCoach(1L);

        assertEquals(ResponseEntity.status(200).body(coachDto), result);
        verify(coachRepository).findById(1L);
        verifyNoMoreInteractions(coachMapper);
        verifyNoMoreInteractions(coachRepository);
    }

    @Test
    void shouldThrowEntityNotFoundException() {
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> coachService.getCoach(99L));
        assertInstanceOf(EntityNotFoundException.class, exception);
    }

    @Test
    void shouldReturnResponseEntityWithAllCoaches() {
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        var firstCoachDto = new CoachDto(1L, new UserDto(
                1L,
                "a@b.com",
                "12345609097",
                "N", roles, date),
                "B",
                "C");

        var firstCoachEntity = new CoachEntity(1L, new UserEntity(
                1L,
                "a@b.com",
                "12345609097",
                null,
                "M", roles,
                null, date,
                null,
                null),
                "B",
                "C",
                null);

        var coachesDto = List.of(firstCoachDto);
        var coachesEntities = List.of(firstCoachEntity);


        when(coachRepository.findAll()).thenReturn(coachesEntities);
        when(coachMapper.toDto(firstCoachEntity)).thenReturn(firstCoachDto);


        var result = coachService.getAllCoaches();

        assertEquals(ResponseEntity.ok(coachesDto), result);
        verify(coachRepository).findAll();
        verifyNoMoreInteractions(coachRepository);
        verifyNoMoreInteractions(coachMapper);
    }

    @Test
    void shouldReturnEmptyListNoException() {
        when(coachRepository.findAll()).thenReturn(List.of());

        var result = coachService.getAllCoaches();

        assertEquals(ResponseEntity.ok(List.of()), result);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhileDeletingCoach() {
        var exception = assertThrows(
                EntityNotFoundException.class,
                () -> coachService.deleteCoach(99L));

        assertEquals(entityNotFoundExceptionMessage("coach", 99L), exception.getMessage());

    }

    @Test
    void shouldReturnResponseEntityWithOkAndOkMessage() {
        var id = 1L;
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        var relatedUserEntity = new UserEntity(
                1L,
                "a@b.com",
                "12345609097",
                "123456",
                "N", roles,
                null, date,
                null,
                null);
        var coachToDeleteEntity = new CoachEntity(
                1L, relatedUserEntity,
                "B",
                "C",
                null);
        var groupEntity = List.of(new GroupEntity(
                1L,
                "G", coachToDeleteEntity,
                null,
                null,
                4));

        when(coachRepository.existsById(id)).thenReturn(true);
        when(coachRepository.getReferenceById(id)).thenReturn(coachToDeleteEntity);
        when(groupRepository.findByCoachId(1L)).thenReturn(groupEntity);


        var result = coachService.deleteCoach(id);

        assertEquals(ResponseEntity
                .ok(Map.of("message", successMessage("coach", 1L, EventStatus.DELETED))), result);
    }

    @Test
    void ShouldThrowExceptionWhileEditingNoExistingCoach() {
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        var id = 99L;
        var user = new UserDto(1L, "a@b.com", "12345609097", "N", roles, date);
        var coach = new CoachDto(99L, user, "B", "C");
        var exception = assertThrows(EntityNotFoundException.class, () -> coachService.editCoach(coach, id));

        assertEquals(entityNotFoundExceptionMessage("coach", 99L), exception.getMessage());
        assertInstanceOf(EntityNotFoundException.class, exception);

    }

    @Test
    void shouldReturnResponseEntityWithEditCoachDto() {
        var id = 1L;
        var coachDtoToSave = new CoachDto(1L, null, "B", "C");
        var coachEntityToSave = new CoachEntity(1L, null, "B", "C", List.of());
        var savedEntity = new CoachEntity(1L, null, "B", "C", List.of());
        var savedDto = new CoachDto(1L, null, "B", "C");
        var returnedDto = new CoachDto(1L, null, "B", "C");

        when(coachRepository.existsById(id)).thenReturn(Boolean.TRUE);
        when(coachMapper.toEntity(coachDtoToSave)).thenReturn(coachEntityToSave);
        when(coachRepository.save(coachEntityToSave)).thenReturn(savedEntity);
        when(coachMapper.toDto(savedEntity)).thenReturn(savedDto);

        var result = coachService.editCoach(coachDtoToSave, id);

        assertEquals(ResponseEntity.ok(returnedDto), result);
        verify(coachRepository).save(coachEntityToSave);
        verifyNoMoreInteractions(coachRepository);
    }


}
