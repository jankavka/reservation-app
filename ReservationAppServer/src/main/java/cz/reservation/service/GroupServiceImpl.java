package cz.reservation.service;

import cz.reservation.dto.GroupDto;
import cz.reservation.dto.mapper.GroupMapper;
import cz.reservation.entity.GroupEntity;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.SeasonRepository;
import cz.reservation.service.serviceinterface.GroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;

    private final GroupRepository groupRepository;

    private final SeasonRepository seasonRepository;

    private final CoachRepository coachRepository;

    @Autowired
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
        if (groupDto == null) {
            throw new IllegalArgumentException("Group must not be null");

        } else {
            GroupEntity entityToSave = groupMapper.toEntity(groupDto);
            setForeignKeys(entityToSave, groupDto);
            var savedEntity = groupRepository.save(entityToSave);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(groupMapper.toDto(savedEntity));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<GroupDto> getGroup(Long id) {
        var entity = groupRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);

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
            throw new EntityNotFoundException("Group doesn't exist.");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteGroup(Long id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);

            var responseMessage = new HashMap<String, String>();
            responseMessage.put("message", "Group with id " + id + " was deleted");

            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        } else {
            throw new EntityNotFoundException("Group doesn't exist.");
        }

    }

    private void setForeignKeys(GroupEntity target, GroupDto source) {
        try {
            target.setCoach(coachRepository.getReferenceById(source.coach().id()));
            target.setSeason(seasonRepository.getReferenceById(source.season().id()));
        } catch (NullPointerException e) {
            log.warn("Coach id is null!");
        }

    }
}
