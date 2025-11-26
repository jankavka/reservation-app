package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.dto.mapper.TrainingSlotMapper;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.TrainingSlotRepository;
import cz.reservation.service.serviceinterface.TrainingSlotService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

public class TrainingSlotServiceImpl implements TrainingSlotService {

    private final TrainingSlotRepository trainingSlotRepository;

    private final TrainingSlotMapper trainingSlotMapper;

    private final CourtRepository courtRepository;

    private final GroupRepository groupRepository;

    private static final String SERVICE_NAME = "training slot";

    private static final String ID = "id";

    public TrainingSlotServiceImpl(
            TrainingSlotMapper trainingSlotMapper,
            TrainingSlotRepository trainingSlotRepository,
            CourtRepository courtRepository,
            GroupRepository groupRepository
    ) {
        this.trainingSlotMapper = trainingSlotMapper;
        this.trainingSlotRepository = trainingSlotRepository;
        this.courtRepository = courtRepository;
        this.groupRepository = groupRepository;

    }

    @Override
    @Transactional
    public ResponseEntity<TrainingSlotDto> createTrainingSlot(TrainingSlotDto trainingSlotDto) {
        if (trainingSlotDto == null) {
            throw new IllegalArgumentException(notNullMessage(SERVICE_NAME));
        } else {
            var entityToSave = trainingSlotMapper.toEntity(trainingSlotDto);
            entityToSave.setCourt(courtRepository.getReferenceById(trainingSlotDto.court().id()));
            entityToSave.setGroup(groupRepository.getReferenceById(trainingSlotDto.group().id()));
            var savedEntity = trainingSlotRepository.save(entityToSave);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(trainingSlotMapper.toDto(savedEntity));

        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<TrainingSlotDto> getTrainingSlot(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(notNullMessage(ID));
        } else if (!trainingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(trainingSlotMapper.toDto(trainingSlotRepository
                            .findById(id)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    entityNotFoundExceptionMessage(SERVICE_NAME, id)))));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<TrainingSlotDto>> getAllTrainingSlots() {
        return ResponseEntity.ok(trainingSlotRepository
                .findAll()
                .stream()
                .map(trainingSlotMapper::toDto)
                .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editTrainingSlot(TrainingSlotDto trainingSlotDto, Long id) {
        if (id == null) {
            throw new IllegalArgumentException(notNullMessage(ID));
        }
        if (trainingSlotDto == null) {
            throw new IllegalArgumentException(notNullMessage(SERVICE_NAME));
        } else if (!trainingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToSave = trainingSlotMapper.toEntity(trainingSlotDto);
            entityToSave.setGroup(groupRepository.getReferenceById(trainingSlotDto.group().id()));
            entityToSave.setCourt(courtRepository.getReferenceById(trainingSlotDto.court().id()));

            trainingSlotRepository.save(entityToSave);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteTrainingSlot(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(notNullMessage(ID));
        }
        if (!trainingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            trainingSlotRepository.deleteById(id);

            return ResponseEntity
                    .ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }
    }
}
