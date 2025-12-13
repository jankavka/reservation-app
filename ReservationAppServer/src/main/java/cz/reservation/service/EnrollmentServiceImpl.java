package cz.reservation.service;

import cz.reservation.constant.EnrollmentState;
import cz.reservation.constant.EventStatus;
import cz.reservation.dto.EnrollmentDto;
import cz.reservation.dto.mapper.EnrollmentMapper;
import cz.reservation.entity.EnrollmentEntity;
import cz.reservation.entity.repository.EnrollmentRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.service.exception.EnrollmentAlreadyCanceledException;
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
        setForeignKeys(entityToSave, enrollmentDto);
        entityToSave.setCreatedAt(LocalDateTime.now());
        var relatedGroup = entityToSave.getGroup();
        var countOfEnrolmentsInGroup = enrollmentRepository.countEnrollmentsByGroupId(relatedGroup.getId());
        if (relatedGroup.getCapacity() > countOfEnrolmentsInGroup) {
            entityToSave.setState(EnrollmentState.ACTIVE);
        } else {
            entityToSave.setState(EnrollmentState.WAITLIST);
        }

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
    public ResponseEntity<EnrollmentDto> editEnrollment(EnrollmentDto enrollmentDto, Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToSave = enrollmentMapper.toEntity(enrollmentDto);
            entityToSave.setId(id);
            EnrollmentEntity savedEntity = enrollmentRepository.save(entityToSave);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(enrollmentMapper.toDto(savedEntity));
        }
    }

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

            //Canceling the enrollment
            enrollmentForCancel.setState(EnrollmentState.CANCELLED);

            //list with all enrollments
            List<EnrollmentEntity> allEnrollments = enrollmentRepository.findAll();

            //Finding the one with EnrollmentState.WAITLIST and lowest date of creation and
            // setting up to EnrollmentState.ACTIVE
            allEnrollments
                    .stream()
                    .filter(enrolment -> enrolment.getState() == EnrollmentState.WAITLIST)
                    .min(Comparator.comparing(EnrollmentEntity::getCreatedAt))
                    .orElseThrow()
                    .setState(EnrollmentState.ACTIVE);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.CANCELED)));
        }
    }

    private void setForeignKeys(EnrollmentEntity target, EnrollmentDto source) {
        if(playerRepository.existsById(source.player().id())){
            target.setPlayer(playerRepository.getReferenceById(source.player().id()));

        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(
                    "Player",source.player().id()));
        }

        if(groupRepository.existsById(source.group().id())){
            target.setGroup(groupRepository.getReferenceById(source.group().id()));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(
                    "Group", source.group().id()));
        }



    }


}
