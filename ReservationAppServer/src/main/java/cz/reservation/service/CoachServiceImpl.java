package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.Role;
import cz.reservation.dto.CoachDto;
import cz.reservation.dto.mapper.CoachMapper;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.CoachService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.NonUniqueObjectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;


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
    public ResponseEntity<CoachDto> createCoach(CoachDto coachDto) throws NonUniqueObjectException {

        var entityToSave = coachMapper.toEntity(coachDto);
        var userId = coachDto.user().id();
        if (userRepository.existsById(userId)) {
            entityToSave.setUser(userRepository.getReferenceById(userId));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("user", userId));
        }
        entityToSave.getUser().getRoles().add(Role.COACH);

        var savedEntity = coachRepository.save(entityToSave);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(coachMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CoachDto> getCoach(Long id) {
        return ResponseEntity.ok(coachMapper.toDto(coachRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)))));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CoachDto>> getAllCoaches() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(coachRepository
                        .findAll()
                        .stream()
                        .map(coachMapper::toDto)
                        .toList());
    }

    /**
     * Method deletes coach from database while related user and group persists. Coach entity
     * is set to null in every related group.
     *
     * @param id of coach entity which will be deleted
     * @return ResponseEntity with status code 200
     */
    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteCoach(Long id) {
        if (coachRepository.existsById(id)) {

            //reference of entity which will be deleted
            var entityToDelete = coachRepository.getReferenceById(id);
            //related user
            var relatedUser = entityToDelete.getUser();
            //removes role from related eser
            relatedUser.getRoles().remove(Role.COACH);
            //removes coach from related groups
            groupRepository.findByCoachId(id).forEach(groupEntity -> groupEntity.setCoach(null));
            //deletes coach itself
            coachRepository.deleteById(id);


            // Message to return
            var responseMessage = new HashMap<String, String>();
            responseMessage.put("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED));

            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<CoachDto> editCoach(CoachDto coachDto, Long id) {
        if (coachRepository.existsById(id)) {
            var entityToSave = coachMapper.toEntity(coachDto);
            entityToSave.setId(id);
            var savedEntity = coachRepository.save(entityToSave);
            return ResponseEntity.ok(coachMapper.toDto(savedEntity));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }


}
