package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.GroupDto;
import cz.reservation.dto.mapper.GroupMapper;
import cz.reservation.entity.GroupEntity;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.SeasonRepository;
import cz.reservation.service.serviceinterface.GroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;

    private final GroupRepository groupRepository;

    private final SeasonRepository seasonRepository;

    private final CoachRepository coachRepository;

    private static final String SERVICE_NAME = "group";

    public GroupServiceImpl(
            GroupRepository groupRepository,
            GroupMapper groupMapper,
            SeasonRepository seasonRepository,
            CoachRepository coachRepository) {
        this.groupMapper = groupMapper;
        this.groupRepository = groupRepository;
        this.seasonRepository = seasonRepository;
        this.coachRepository = coachRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<GroupDto> createGroup(GroupDto groupDto) {

        var entityToSave = groupMapper.toEntity(groupDto);
        var seasonId = groupDto.season().id();
        var coachId = groupDto.coach() == null ? null : groupDto.coach().id();

        if (seasonRepository.existsById(seasonId)) {
            if (coachId != null && coachRepository.existsById(coachId)) {
                setForeignKeys(entityToSave, groupDto);
            } else {
                throw new EntityNotFoundException(entityNotFoundExceptionMessage("coach", coachId));
            }
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("season", seasonId));

        }

        var savedEntity = groupRepository.save(entityToSave);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<GroupDto> getGroup(Long id) {

        var entity = groupRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id)));

        return ResponseEntity.status(HttpStatus.OK).body(groupMapper.toDto(entity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupRepository.findAll().stream()
                        .map(groupMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<GroupDto> editGroup(GroupDto groupDto, Long id) {
        if (groupRepository.existsById(id)) {
            var entityToSave = groupMapper.toEntity(groupDto);
            setForeignKeys(entityToSave, groupDto);
            var savedEntity = groupRepository.save(entityToSave);
            return ResponseEntity.status(HttpStatus.OK).body(groupMapper.toDto(savedEntity));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteGroup(Long id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);

            var responseMessage = new HashMap<String, String>();
            responseMessage.put("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED));

            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }

    }

    private void setForeignKeys(GroupEntity target, GroupDto source) {
        if (source.coach() != null) {
            target.setCoach(coachRepository.getReferenceById(source.coach().id()));
        }
        target.setSeason(seasonRepository.getReferenceById(source.season().id()));
    }

}

