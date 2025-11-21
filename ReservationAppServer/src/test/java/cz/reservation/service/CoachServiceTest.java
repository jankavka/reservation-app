package cz.reservation.service;

import cz.reservation.constant.Role;
import cz.reservation.dto.CoachDto;
import cz.reservation.dto.UserDto;
import cz.reservation.dto.mapper.CoachMapper;
import cz.reservation.entity.CoachEntity;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachServiceTest {

    @Mock
    CoachRepository coachRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CoachMapper coachMapper;

    @InjectMocks
    CoachServiceImpl coachService;


    Date date = Date.from(LocalDate.of(2025, 8, 10)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant());


    @Test
    void shouldReturnCoachDtoAndStatusOk() {
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        var relatedUserDto = new UserDto(
                1L,
                "example@email.com",
                "N",
                roles,
                date);
        var relatedUserEntity = new UserEntity(
                1L,
                "example@email.com",
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
    }

    @Test
    void shouldReturnResponseEntityWithAllCoaches() {
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        var firstCoachDto = new CoachDto(1L, new UserDto(
                1L, "a@b.com", "N", roles, date), "B", "C");
        var firstCoachEntity = new CoachEntity(1L, new UserEntity(
                1L, "a@b.com", null, "M", roles, null, date, null, null), "B", "C", null);
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
}
