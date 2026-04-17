package cz.reservation.service;

import cz.reservation.constant.EnrollmentState;
import cz.reservation.constant.PricingType;
import cz.reservation.dto.EnrollmentDto;
import cz.reservation.dto.GroupDto;
import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.mapper.EnrollmentMapper;
import cz.reservation.entity.EnrollmentEntity;
import cz.reservation.entity.GroupEntity;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.filter.EnrollmentFilter;
import cz.reservation.entity.repository.EnrollmentRepository;
import cz.reservation.entity.repository.specification.EnrollmentSpecification;
import cz.reservation.service.exception.MissingPricingTypeException;
import cz.reservation.service.serviceinterface.GroupService;
import cz.reservation.service.serviceinterface.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    EnrollmentMapper enrollmentMapper;

    @Mock
    GroupService groupService;

    @Mock
    PlayerService playerService;

    @InjectMocks
    EnrollmentServiceImpl service;


    @Test
    void shouldCreateEnrollment() {
        var playerEntity = new PlayerEntity(
                1L, null, null, null, null, PricingType.PER_SLOT,
                null, null, null, null, null);
        var groupEntity = new GroupEntity(1L, null, null, null, null, 4);
        var enrollmentEntity = new EnrollmentEntity(1L, null, null, null, null);
        var playerDto = new PlayerDto(
                1L, null, null, null,
                null, PricingType.PER_SLOT, null, null);
        var groupDto = new GroupDto(1L, null, null, null, 4, null);
        var enrollmentDto = new EnrollmentDto(1L, playerDto, groupDto, null, null);
        var createdAt = LocalDateTime.now();
        var savedEnrollmentEntity = new EnrollmentEntity(
                1L, playerEntity, groupEntity, EnrollmentState.ACTIVE, createdAt);
        var savedEnrollmentDto = new EnrollmentDto(1L, playerDto, groupDto, EnrollmentState.ACTIVE, createdAt);


        when(enrollmentMapper.toEntity(enrollmentDto)).thenReturn(enrollmentEntity);
        when(groupService.getGroupEntity(enrollmentDto.group().id())).thenReturn(groupEntity);
        when(playerService.getPlayerEntity(enrollmentDto.player().id())).thenReturn(playerEntity);
        when(groupService.getGroupEntity(enrollmentDto.group().id())).thenReturn(groupEntity);
        when(enrollmentRepository.save(enrollmentEntity)).thenReturn(savedEnrollmentEntity);
        when(enrollmentMapper.toDto(savedEnrollmentEntity)).thenReturn(savedEnrollmentDto);

        var result = service.createEnrollment(enrollmentDto);

        assertEquals(savedEnrollmentDto.createdAt(), result.createdAt());
        assertEquals(savedEnrollmentDto.id(), result.id());
        assertEquals(EnrollmentState.ACTIVE, enrollmentEntity.getState());
        assertEquals(playerEntity.getId(), enrollmentEntity.getPlayer().getId());
        assertEquals(groupEntity.getId(), enrollmentEntity.getGroup().getId());

        verify(enrollmentMapper).toEntity(enrollmentDto);
        verify(groupService, times(2)).getGroupEntity(enrollmentDto.group().id());
        verify(playerService).getPlayerEntity(enrollmentDto.player().id());
        verify(enrollmentRepository).save(enrollmentEntity);
        verify(enrollmentMapper).toDto(savedEnrollmentEntity);
        verifyNoMoreInteractions(enrollmentMapper);
        verifyNoMoreInteractions(groupService);
        verifyNoMoreInteractions(playerService);

    }

    @Test
    void shouldThrowEntityNotFoundException_whileNoGroupFound() {
        var playerEntity = new PlayerEntity(
                1L, null, null, null, null, PricingType.PER_SLOT,
                null, null, null, null, null);
        var groupEntity = new GroupEntity(1L, null, null, null, null, 4);
        var enrollmentEntity = new EnrollmentEntity(1L, playerEntity, groupEntity, null, null);
        var playerDto = new PlayerDto(
                1L, null, null, null,
                null, PricingType.PER_SLOT, null, null);
        var groupDto = new GroupDto(1L, null, null, null, 4, null);
        var enrollmentDto = new EnrollmentDto(1L, playerDto, groupDto, null, null);

        when(enrollmentMapper.toEntity(enrollmentDto)).thenReturn(enrollmentEntity);
        when(groupService.getGroupEntity(enrollmentDto.group().id())).thenThrow(EntityNotFoundException.class);

        var exception = assertThrows(EntityNotFoundException.class, () -> service.createEnrollment(enrollmentDto));

        assertInstanceOf(EntityNotFoundException.class, exception);

        verify(enrollmentMapper, times(1)).toEntity(enrollmentDto);
        verify(groupService, times(1)).getGroupEntity(enrollmentDto.group().id());
        verifyNoMoreInteractions(groupService);
        verifyNoMoreInteractions(enrollmentMapper);
        verifyNoInteractions(enrollmentRepository);
        verifyNoInteractions(playerService);
    }

    @Test
    void shouldThrowMissingPricingTypeException() {
        var playerEntity = new PlayerEntity(
                1L, null, null, null, null, null,
                null, null, null, null, null);
        var groupEntity = new GroupEntity(1L, null, null, null, null, 4);
        var enrollmentEntity = new EnrollmentEntity(1L, playerEntity, groupEntity, null, null);
        var playerDto = new PlayerDto(
                1L, null, null, null,
                null, null, null, null);
        var groupDto = new GroupDto(1L, null, null, null, 4, null);
        var enrollmentDto = new EnrollmentDto(1L, playerDto, groupDto, null, null);

        when(enrollmentMapper.toEntity(enrollmentDto)).thenReturn(enrollmentEntity);
        when(groupService.getGroupEntity(enrollmentDto.group().id())).thenReturn(groupEntity);
        when(playerService.getPlayerEntity(enrollmentDto.player().id())).thenReturn(playerEntity);

        var exception = assertThrows(MissingPricingTypeException.class, () -> service.createEnrollment(enrollmentDto));

        assertInstanceOf(MissingPricingTypeException.class, exception);

        assertEquals(
                "There is no pricing type for player id "
                        + enrollmentDto.player().id() +
                        ". You have to pick it first or buy a package",
                exception.getMessage());

        verify(enrollmentMapper, times(1)).toEntity(enrollmentDto);
        verify(groupService, times(1)).getGroupEntity(enrollmentDto.group().id());
        verify(playerService, times(1)).getPlayerEntity(enrollmentDto.player().id());
        verifyNoMoreInteractions(enrollmentMapper);
        verifyNoMoreInteractions(playerService);
        verifyNoMoreInteractions(groupService);


    }

    @Test
    void shouldCreateAndReturnEnrollmentWithStatusWaitlist() {
        var playerEntity = new PlayerEntity(
                1L, null, null, null, null, PricingType.PER_SLOT,
                null, null, null, null, null);
        var groupEntity = new GroupEntity(1L, null, null, null, null, 4);
        var enrollmentEntity = new EnrollmentEntity(1L, null, null, null, null);
        var playerDto = new PlayerDto(
                1L, null, null, null,
                null, PricingType.PER_SLOT, null, null);
        var groupDto = new GroupDto(1L, null, null, null, 4, null);
        var enrollmentDto = new EnrollmentDto(1L, playerDto, groupDto, null, null);
        var createdAt = LocalDateTime.now();
        var savedEnrollmentEntity = new EnrollmentEntity(
                1L, playerEntity, groupEntity, enrollmentEntity.getState(), createdAt);
        var savedEnrollmentDto = new EnrollmentDto(
                1L, playerDto, groupDto, savedEnrollmentEntity.getState(), createdAt);

        when(enrollmentMapper.toEntity(enrollmentDto)).thenReturn(enrollmentEntity);
        when(groupService.getGroupEntity(enrollmentDto.group().id())).thenReturn(groupEntity);
        when(playerService.getPlayerEntity(enrollmentDto.player().id())).thenReturn(playerEntity);
        when(groupService.getGroupEntity(enrollmentDto.group().id())).thenReturn(groupEntity);
        when(enrollmentRepository.countEnrollmentsByGroupId(groupEntity.getId())).thenReturn(4);
        when(enrollmentRepository.save(enrollmentEntity)).thenReturn(savedEnrollmentEntity);
        when(enrollmentMapper.toDto(savedEnrollmentEntity)).thenReturn(savedEnrollmentDto);


        var result = service.createEnrollment(enrollmentDto);

        assertEquals(EnrollmentState.WAITLIST, enrollmentEntity.getState());
        assertEquals(savedEnrollmentDto.id(), result.id());
        assertEquals(savedEnrollmentDto.player().id(), result.player().id());

        verify(enrollmentMapper).toEntity(enrollmentDto);
        verify(groupService, times(2)).getGroupEntity(enrollmentDto.group().id());
        verify(playerService).getPlayerEntity(any(Long.class));
        verify(enrollmentRepository).countEnrollmentsByGroupId(any(Long.class));
        verify(enrollmentRepository).save(enrollmentEntity);
        verify(enrollmentMapper).toDto(savedEnrollmentEntity);


    }

    @Test
    void shouldGetEnrollment(){
        var id = 1L;
        var date  = LocalDateTime.now();
        var enrollmentEntity = new EnrollmentEntity();
        enrollmentEntity.setId(id);
        enrollmentEntity.setState(EnrollmentState.ACTIVE);
        enrollmentEntity.setCreatedAt(date);
        var enrollmentDto = new EnrollmentDto(1L,null,null,EnrollmentState.ACTIVE,date);

        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(enrollmentEntity));
        when(enrollmentMapper.toDto(enrollmentEntity)).thenReturn(enrollmentDto);

        var result = service.getEnrollment(id);

        assertEquals(enrollmentDto.id(), result.id());
        assertEquals(enrollmentDto.createdAt(), result.createdAt());
        assertEquals(enrollmentDto.state(), result.state());

        verify(enrollmentMapper).toDto(enrollmentEntity);
        verify(enrollmentRepository).findById(id);

    }

    @Test
    void shouldThrowEntityNotFoundException_whileNoSuchEnrollment(){
        var id = 99L;

        var exception = assertThrows(EntityNotFoundException.class, () -> service.getEnrollment(id));

        assertInstanceOf(EntityNotFoundException.class, exception);

        assertEquals(entityNotFoundExceptionMessage("enrollment", id), exception.getMessage());

        verify(enrollmentRepository, times(1)).findById(id);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    void shouldReturnAllEnrollments(){
        var enrollmentEntity1 = new EnrollmentEntity(1L,null,null,null,null);
        var enrollmentEntity2 = new EnrollmentEntity(2L,null,null,null,null);
        var enrollmentEntity3 = new EnrollmentEntity(3L,null,null,null,null);
        var enrollmentDto1 = new EnrollmentDto(1L, null,null,null,null);
        var enrollmentDto2 = new EnrollmentDto(2L, null,null,null,null);
        var enrollmentDto3 = new EnrollmentDto(3L, null,null,null,null);

        var enrollmentEntities = new ArrayList<EnrollmentEntity>();

        enrollmentEntities.add(enrollmentEntity1);
        enrollmentEntities.add(enrollmentEntity2);
        enrollmentEntities.add(enrollmentEntity3);

        var enrollmentDtos = new ArrayList<EnrollmentDto>();

        enrollmentDtos.add(enrollmentDto1);
        enrollmentDtos.add(enrollmentDto2);
        enrollmentDtos.add(enrollmentDto3);

        var filter= new EnrollmentFilter(null, null,null,null,null);

        when(enrollmentMapper.toDto(enrollmentEntity1)).thenReturn(enrollmentDto1);
        when(enrollmentMapper.toDto(enrollmentEntity2)).thenReturn(enrollmentDto2);
        when(enrollmentMapper.toDto(enrollmentEntity3)).thenReturn(enrollmentDto3);
        when(enrollmentRepository.findAll(any(EnrollmentSpecification.class))).thenReturn(enrollmentEntities);

        var result = service.getAllEnrollments(filter);

        assertEquals(enrollmentDtos.get(1).id(), result.get(1).id());

    }

    @Test
    void shouldReturnEmptyList(){
        var emptyEnrollmentEntityList = new ArrayList<EnrollmentEntity>();
        var emptyEnrollmentDtoList = new ArrayList<EnrollmentDto>();
        var filter = new EnrollmentFilter(null,null,null,null,null);
        when(enrollmentRepository.findAll(any(EnrollmentSpecification.class))).thenReturn(emptyEnrollmentEntityList);

        var result = service.getAllEnrollments(filter);

        assertEquals(emptyEnrollmentDtoList, result);

        verify(enrollmentRepository, times(1)).findAll(any(EnrollmentSpecification.class));
        verifyNoInteractions(enrollmentMapper);
        verifyNoMoreInteractions(enrollmentRepository);


    }


}
