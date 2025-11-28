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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
public class TrainingSlotServiceImpl implements TrainingSlotService {

    private final TrainingSlotRepository trainingSlotRepository;

    private final TrainingSlotMapper trainingSlotMapper;

    private final CourtRepository courtRepository;

    private final GroupRepository groupRepository;

    private static final String SERVICE_NAME = "training slot";

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

        var entityToSave = trainingSlotMapper.toEntity(trainingSlotDto);
        var groupId = trainingSlotDto.group().id();
        var courtId = trainingSlotDto.court().id();

        //checking existing related group
        if (groupRepository.existsById(groupId)) {
            entityToSave.setGroup(groupRepository.getReferenceById(trainingSlotDto.group().id()));
            //setting the same capacity as related group
            entityToSave.setCapacity(entityToSave.getGroup().getCapacity());
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(
                    "group", trainingSlotDto.group().id()));
        }
        if (courtRepository.existsById(courtId)) {
            entityToSave.setCourt(courtRepository.getReferenceById(courtId));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("court", courtId));
        }





        var savedEntity = trainingSlotRepository.save(entityToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(trainingSlotMapper.toDto(savedEntity));


    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<TrainingSlotDto> getTrainingSlot(Long id) {
        if (!trainingSlotRepository.existsById(id)) {
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
        if (!trainingSlotRepository.existsById(id)) {
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
        if (!trainingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            trainingSlotRepository.deleteById(id);

            return ResponseEntity
                    .ok(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }
    }
}
