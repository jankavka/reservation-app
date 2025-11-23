package cz.reservation.service;

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

public class TrainingSlotServiceImpl implements TrainingSlotService {

    private final TrainingSlotRepository trainingSlotRepository;

    private final TrainingSlotMapper trainingSlotMapper;

    private final CourtRepository courtRepository;

    private final GroupRepository groupRepository;

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
            throw new IllegalArgumentException("Training slot must not be null");
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
            throw new IllegalArgumentException("Id must not be null");
        } else if (!trainingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException("Training slot not found");
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(trainingSlotMapper.toDto(trainingSlotRepository
                            .findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Training slot not found"))));
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
        if (trainingSlotDto == null) {
            throw new IllegalArgumentException("Training slot must not be null");
        } else if (!trainingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException("Training slot with id " + id + " not exist");
        } else {
            var entityToSave = trainingSlotMapper.toEntity(trainingSlotDto);
            entityToSave.setGroup(groupRepository.getReferenceById(trainingSlotDto.group().id()));
            entityToSave.setCourt(courtRepository.getReferenceById(trainingSlotDto.court().id()));

            trainingSlotRepository.save(entityToSave);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "Training Slot with id " + id + " successfully edited"));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteTrainingSlot(Long id) {
        if (!trainingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException("Training slot not found");
        } else {
            trainingSlotRepository.deleteById(id);

            return ResponseEntity
                    .ok(Map.of("message", "Training slot with id " + id + " successfully deleted"));
        }
    }
}
