package cz.reservation.service;

import cz.reservation.dto.GroupDto;
import cz.reservation.dto.mapper.GroupMapper;
import cz.reservation.entity.GroupEntity;
import cz.reservation.entity.filter.GroupFilter;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.SeasonRepository;
import cz.reservation.entity.repository.specification.GroupSpecification;
import cz.reservation.service.serviceinterface.GroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final GroupRepository groupRepository;
    private final SeasonRepository seasonRepository;
    private final CoachRepository coachRepository;

    private static final String SERVICE_NAME = "group";

    @Override
    @Transactional
    public GroupDto createGroup(GroupDto groupDto) {
        var entityToSave = groupMapper.toEntity(groupDto);
        setForeignKeys(entityToSave, groupDto);
        var savedEntity = groupRepository.save(entityToSave);
        return groupMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupDto getGroup(Long id) {
        var entity = groupRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        return groupMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupDto> getAllGroups(GroupFilter groupFilter) {
        var spec = new GroupSpecification(groupFilter);
        return groupRepository.findAll(spec).stream()
                .map(groupMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void editGroup(GroupDto groupDto, Long id) {
        var entityToUpdate = groupRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        groupMapper.updateEntity(entityToUpdate, groupDto);
        setForeignKeys(entityToUpdate, groupDto);
    }

    @Override
    @Transactional
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        groupRepository.deleteById(id);
    }

    private void setForeignKeys(GroupEntity target, GroupDto source) {
        if (source.coach() != null) {
            target.setCoach(coachRepository
                    .findById(source.coach().id())
                    .orElseThrow(
                            () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                                    "coach", source.coach().id()))));
        }
        target.setSeason(seasonRepository
                .findById(source.season().id())
                .orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                                "season", source.season().id()))));
    }
}
