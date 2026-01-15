package cz.reservation.service;

import cz.reservation.constant.EnrollmentState;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

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
    public EnrollmentDto createEnrollment(EnrollmentDto enrollmentDto) {
        var entityToSave = enrollmentMapper.toEntity(enrollmentDto);
        var relatedGroup = entityToSave.getGroup();

        setForeignKeys(entityToSave, enrollmentDto);
        entityToSave.setCreatedAt(LocalDateTime.now());
        setUpState(entityToSave, relatedGroup);

        EnrollmentEntity savedEntity = enrollmentRepository.save(entityToSave);
        return enrollmentMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentDto getEnrollment(Long id) {
        return enrollmentMapper.toDto(enrollmentRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDto> getAllEnrollments() {
        return enrollmentRepository
                .findAll()
                .stream()
                .map(enrollmentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void editEnrollment(EnrollmentDto enrollmentDto, Long id) {
        var entityToUpdate = enrollmentRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        enrollmentMapper.updateEntity(entityToUpdate, enrollmentDto);
        setForeignKeys(entityToUpdate, enrollmentDto);
    }

    @Override
    @Transactional
    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        if (enrollmentRepository.getReferenceById(id).getState() == EnrollmentState.CANCELLED) {
            throw new EnrollmentAlreadyCanceledException("Enrollment with id " + id + " already canceled");
        }

        var enrollmentForCancel = enrollmentRepository.getReferenceById(id);
        var relatedGroupId = enrollmentForCancel.getGroup().getId();

        enrollmentForCancel.setState(EnrollmentState.CANCELLED);

        List<EnrollmentEntity> allEnrollmentsByGroup = enrollmentRepository.findByGroupId(relatedGroupId);
        setEnrollmentActive(allEnrollmentsByGroup);
    }

    private void setForeignKeys(EnrollmentEntity target, EnrollmentDto source) {
        var player = playerRepository.findById(source.player().id()).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage("Player", source.player().id())));

        if (player.getPricingType() == null) {
            throw new MissingPricingTypeException("There is no pricing type for player id "
                    + player.getId() + ". You have to pick it first or buy a package");
        }
        target.setPlayer(playerRepository.getReferenceById(source.player().id()));

        var group = groupRepository.findById(source.group().id()).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage("Group", source.group().id())));
        target.setGroup(groupRepository.getReferenceById(group.getId()));
    }

    private void setEnrollmentActive(List<EnrollmentEntity> allEnrollments) {
        allEnrollments
                .stream()
                .filter(enrolment -> enrolment.getState() == EnrollmentState.WAITLIST)
                .min(Comparator.comparing(EnrollmentEntity::getCreatedAt))
                .ifPresent(enrollment -> enrollment.setState(EnrollmentState.ACTIVE));
    }

    private void setUpState(EnrollmentEntity target, GroupEntity relatedGroup) {
        var countOfEnrolmentsInGroup = enrollmentRepository.countEnrollmentsByGroupId(relatedGroup.getId());
        var condition = relatedGroup.getCapacity() > countOfEnrolmentsInGroup;
        target.setState(condition ? EnrollmentState.ACTIVE : EnrollmentState.WAITLIST);
    }
}
