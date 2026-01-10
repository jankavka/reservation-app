package cz.reservation.service;

import cz.reservation.constant.EnrollmentState;
import cz.reservation.constant.EventStatus;
import cz.reservation.dto.EnrollmentDto;
import cz.reservation.dto.mapper.EnrollmentMapper;
import cz.reservation.entity.EnrollmentEntity;
import cz.reservation.entity.GroupEntity;
import cz.reservation.entity.repository.EnrollmentRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.service.exception.EnrollmentAlreadyCanceledException;
import cz.reservation.service.exception.MissingPricingTypeException;
import cz.reservation.service.serviceinterface.EnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final EnrollmentMapper enrollmentMapper;

    private final GroupRepository groupRepository;

    private final PlayerRepository playerRepository;

    private static final String SERVICE_NAME = "enrollment";

    @Override
    @Transactional
    public ResponseEntity<EnrollmentDto> createEnrollment(EnrollmentDto enrollmentDto) {

        var entityToSave = enrollmentMapper.toEntity(enrollmentDto);
        var relatedGroup = entityToSave.getGroup();

        setForeignKeys(entityToSave, enrollmentDto);
        entityToSave.setCreatedAt(LocalDateTime.now());

        setUpState(entityToSave, relatedGroup);

        EnrollmentEntity savedEntity = enrollmentRepository.save(entityToSave);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(enrollmentMapper.toDto(savedEntity));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EnrollmentDto> getEnrollment(Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(enrollmentMapper.toDto(enrollmentRepository
                        .findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                entityNotFoundExceptionMessage(SERVICE_NAME, id)))));


    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EnrollmentDto>> getAllEnrollments() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(enrollmentRepository
                        .findAll()
                        .stream()
                        .map(enrollmentMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editEnrollment(EnrollmentDto enrollmentDto, Long id) {
        var entityToUpdate = enrollmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));

        enrollmentMapper.updateEntity(entityToUpdate, enrollmentDto);


        setForeignKeys(entityToUpdate, enrollmentDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED)));

    }

    /**
     * This method doesn't delete concrete "enrollmentEntity" represented by id, but it sets up
     * its "state" to "EnrollmentState.CANCELLED. Also, the enrollment with the earliest value of date
     * in current group, which have state set to "EnrollmentState.WAITLIST" is set to "EnrollmentState.ACTIVE".
     * In other words the first enrollment on waitlist is added to group as active.
     *
     * @param id represents concrete object in database
     * @return ResponseEntity with message about successful result of operation if no Exception was thrown
     */
    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteEnrollment(Long id) {

        if (!enrollmentRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else if (enrollmentRepository.getReferenceById(id).getState() == EnrollmentState.CANCELLED) {
            throw new EnrollmentAlreadyCanceledException("Enrollment with id " + id + " already canceled");
        } else {
            //Current enrollment entity
            var enrollmentForCancel = enrollmentRepository.getReferenceById(id);
            var relatedGroupId = enrollmentForCancel.getGroup().getId();

            //Canceling the enrollment
            enrollmentForCancel.setState(EnrollmentState.CANCELLED);

            //List with all enrollments connected with concrete group
            List<EnrollmentEntity> allEnrollmentsByGroup = enrollmentRepository.findByGroupId(relatedGroupId);

            //Finding the one with EnrollmentState.WAITLIST and lowest date of creation and
            // setting up to EnrollmentState.ACTIVE
            setEnrollmentActive(allEnrollmentsByGroup);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.CANCELED)));
        }
    }

    /**
     * Sets foreign keys as "playerEntity" and "groupEntity" to current enrollment. Also checks if related
     * player has picked pricing type. If there is no "pricingType" an MissingPricingTypeException is thrown.
     * Player has pick"pricingType" before he makes enrollment to the training group.
     *
     * @param target entity we set foreign keys for
     * @param source Dto with data of foreign keys to set up
     */
    private void setForeignKeys(EnrollmentEntity target, EnrollmentDto source) {
        var player = playerRepository.findById(source.player().id()).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                        "Player", source.player().id())));

        if (player.getPricingType() == null) {
            throw new MissingPricingTypeException("There is no pricing type for player id "
                    + player.getId() + ". You have to pick it first or buy a package");
        } else {
            target.setPlayer(playerRepository.getReferenceById(source.player().id()));
        }

        var group = groupRepository.findById(source.group().id()).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                        "Group", source.group().id())));
        target.setGroup(groupRepository.getReferenceById(group.getId()));
    }

    private void setEnrollmentActive(List<EnrollmentEntity> allEnrollments) {
        allEnrollments
                .stream()
                .filter(enrolment -> enrolment.getState() == EnrollmentState.WAITLIST)
                .min(Comparator.comparing(EnrollmentEntity::getCreatedAt))
                .orElseThrow()
                .setState(EnrollmentState.ACTIVE);
    }


    /**
     * Sets up state to current "enrollmentEntity" based on number of active enrollments
     * in related group. If the number of active enrollments is lower that capacity of
     * the group then "EnrollmentState.ACTIVE" is set, otherwise "EnrollmentState.WAITLIST"
     * is set.
     *
     * @param target       "EnrollmentEntity" object the state is set for
     * @param relatedGroup "GroupEntity" object with related group
     */
    private void setUpState(EnrollmentEntity target, GroupEntity relatedGroup) {
        var countOfEnrolmentsInGroup = enrollmentRepository.countEnrollmentsByGroupId(relatedGroup.getId());
        var condition = relatedGroup.getCapacity() > countOfEnrolmentsInGroup;

        target.setState(condition ? EnrollmentState.ACTIVE : EnrollmentState.WAITLIST);

    }


}
