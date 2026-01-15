package cz.reservation.service;

import cz.reservation.constant.Role;
import cz.reservation.dto.CoachDto;
import cz.reservation.dto.mapper.CoachMapper;
import cz.reservation.entity.CoachEntity;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.CoachService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;


@Service
@RequiredArgsConstructor
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;

    private final CoachMapper coachMapper;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private static final String SERVICE_NAME = "coach";

    @Override
    @Transactional
    public CoachDto createCoach(CoachDto coachDto) {
        var entityToSave = coachMapper.toEntity(coachDto);
        setForeignKeys(entityToSave, coachDto);
        entityToSave.getUser().getRoles().add(Role.COACH);

        var savedEntity = coachRepository.save(entityToSave);
        return coachMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CoachDto getCoach(Long id) {
        return coachMapper.toDto(coachRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachDto> getAllCoaches() {
        return coachRepository
                .findAll()
                .stream()
                .map(coachMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteCoach(Long id) {
        if (!coachRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }

        var entityToDelete = coachRepository.getReferenceById(id);
        var relatedUser = entityToDelete.getUser();
        relatedUser.getRoles().remove(Role.COACH);
        groupRepository.findByCoachId(id).forEach(groupEntity -> groupEntity.setCoach(null));
        coachRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void editCoach(CoachDto coachDto, Long id) {
        var entityToUpdate = coachRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        coachMapper.updateEntity(entityToUpdate, coachDto);
        setForeignKeys(entityToUpdate, coachDto);
    }

    private void setForeignKeys(CoachEntity target, CoachDto source) {
        var userId = source.user().id();
        target.setUser(userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage("user", userId))));
    }


}
