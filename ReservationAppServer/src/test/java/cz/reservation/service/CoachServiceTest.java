package cz.reservation.service;

import cz.reservation.constant.Role;
import cz.reservation.dto.CoachDto;
import cz.reservation.dto.UserDto;
import cz.reservation.dto.mapper.CoachMapper;
import cz.reservation.entity.CoachEntity;
import cz.reservation.entity.GroupEntity;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void shouldCreateCoachAndReturnDto() {
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
                null);
        var coachToSaveDto = new CoachDto(
                null,
                relatedUserDto,
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
        when(userRepository.findById(relatedUserDto.id())).thenReturn(Optional.of(relatedUserEntity));
        when(coachRepository.save(coachToSaveEntity)).thenReturn(savedCoachEntity);
        when(coachMapper.toDto(savedCoachEntity)).thenReturn(savedCoachDto);

        var result = coachService.createCoach(coachToSaveDto);

        assertEquals(savedCoachDto, result);
        verify(coachRepository).save(coachToSaveEntity);
    }

    @Test
    void shouldReturnCoachDto() {
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

        when(coachRepository.findById(id)).thenReturn(Optional.of(coachEntity));
        when(coachMapper.toDto(coachEntity)).thenReturn(coachDto);

        var result = coachService.getCoach(id);

        assertEquals(coachDto, result);
        verify(coachRepository).findById(id);
        verify(coachMapper).toDto(coachEntity);
    }

    @Test
    void shouldThrowEntityNotFoundException() {
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> coachService.getCoach(99L));
        assertInstanceOf(EntityNotFoundException.class, exception);
    }

    @Test
    void shouldReturnAllCoaches() {
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        var firstUserDto = new UserDto(
                1L,
                "a@b.com",
                "12345609097",
                "N",
                roles,
                date);
        var firstUserEntity = new UserEntity(
                1L,
                "a@b.com",
                "12345609097",
                null,
                "M",
                roles,
                null,
                date,
                null);
        var firstCoachDto = new CoachDto(
                1L,
                firstUserDto,
                "B",
                "C");
        var firstCoachEntity = new CoachEntity(
                1L,
                firstUserEntity,
                "B",
                "C",
                null);
        var coachesDto = List.of(firstCoachDto);
        var coachesEntities = List.of(firstCoachEntity);

        when(coachRepository.findAll()).thenReturn(coachesEntities);
        when(coachMapper.toDto(firstCoachEntity)).thenReturn(firstCoachDto);

        var result = coachService.getAllCoaches();

        assertEquals(coachesDto, result);
        verify(coachRepository).findAll();
        verify(coachMapper).toDto(firstCoachEntity);
    }

    @Test
    void shouldReturnEmptyListWhenNoCoaches() {
        when(coachRepository.findAll()).thenReturn(List.of());

        var result = coachService.getAllCoaches();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhileDeletingCoach() {
        var exception = assertThrows(
                EntityNotFoundException.class,
                () -> coachService.deleteCoach(99L));

        assertEquals(entityNotFoundExceptionMessage("coach", 99L), exception.getMessage());
    }

    @Test
    void shouldDeleteCoachSuccessfully() {
        var id = 1L;
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        roles.add(Role.COACH);
        var relatedUserEntity = new UserEntity(
                1L,
                "a@b.com",
                "12345609097",
                "123456",
                "N",
                roles,
                null,
                date,
                null);
        var coachToDeleteEntity = new CoachEntity(
                1L,
                relatedUserEntity,
                "B",
                "C",
                null);
        var groupEntities = List.of(new GroupEntity(
                1L,
                "G",
                coachToDeleteEntity,
                null,
                null,
                4));

        when(coachRepository.existsById(id)).thenReturn(true);
        when(coachRepository.getReferenceById(id)).thenReturn(coachToDeleteEntity);
        when(groupRepository.findByCoachId(id)).thenReturn(groupEntities);

        assertDoesNotThrow(() -> coachService.deleteCoach(id));

        assertFalse(relatedUserEntity.getRoles().contains(Role.COACH));
        assertNull(groupEntities.get(0).getCoach());
        verify(coachRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenEditingNonExistingCoach() {
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        var id = 99L;
        var user = new UserDto(
                1L,
                "a@b.com",
                "12345609097",
                "N",
                roles,
                date);
        var coach = new CoachDto(
                99L,
                user,
                "B",
                "C");

        var exception = assertThrows(EntityNotFoundException.class, () -> coachService.editCoach(coach, id));

        assertEquals(entityNotFoundExceptionMessage("coach", 99L), exception.getMessage());
    }

    @Test
    void shouldEditCoachSuccessfully() {
        var id = 1L;
        var relatedUserDto = new UserDto(
                1L,
                "a@b.cz",
                "123456789",
                "N",
                null,
                date);
        var relatedUserEntity = new UserEntity(
                1L,
                "a@b.cz",
                "123456789",
                null,
                "N",
                null,
                null,
                date,
                null);
        var coachDtoToSave = new CoachDto(
                1L,
                relatedUserDto,
                "B",
                "C");
        var coachEntityToUpdate = new CoachEntity(
                1L,
                relatedUserEntity,
                "B",
                "C",
                null);

        when(coachRepository.findById(id)).thenReturn(Optional.of(coachEntityToUpdate));
        when(userRepository.findById(id)).thenReturn(Optional.of(relatedUserEntity));

        assertDoesNotThrow(() -> coachService.editCoach(coachDtoToSave, id));

        verify(coachRepository).findById(id);
        verify(coachMapper).updateEntity(coachEntityToUpdate, coachDtoToSave);
        verify(userRepository).findById(id);
    }
}
