package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.SlotStatus;
import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.dto.mapper.TrainingSlotMapper;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.TrainingSlotRepository;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.exception.TrainingSlotsInCollisionException;
import cz.reservation.service.serviceinterface.TrainingSlotService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
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

    /**
     * Method creates new training slot in database if there are existing related court and group and
     * there are no time collisions between current slot and all already existing with status OPEN (SlotStatus).
     * Otherwise, it throws EntityNoFindException for missing court or group or TrainingSlotsInCollisionException
     * if there are slots in time collision with the current one.
     *
     * @param trainingSlotDto data object with new training slot
     * @return ResponseEntity with status CREATED and created object as an instance of TrainingSlotDto
     */
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

        //checking existing related court
        if (courtRepository.existsById(courtId)) {
            entityToSave.setCourt(courtRepository.getReferenceById(courtId));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("court", courtId));
        }

        var allTrainingSlots = trainingSlotRepository.findAll();
        var currentSlotStartAtMillis = trainingSlotDto.startAt().toEpochSecond(ZoneOffset.UTC);
        var currentSlotEndAtMillis = trainingSlotDto.endAt().toEpochSecond(ZoneOffset.UTC);

        //looking for collisions between current slot and all already saved slots with OPEN status
        var allCollisionSlots = allTrainingSlots
                .stream()
                //finds slots with status OPEN and corresponding court
                .filter(slot -> slot.getCourt().getId().equals(courtId) && slot.getStatus() == SlotStatus.OPEN)
                .map(slot -> List.of(slot.getStartAt().toEpochSecond(ZoneOffset.UTC), slot.getEndAt().toEpochSecond(ZoneOffset.UTC)))
                //finds all slots, which are in collision with the current one
                .filter(slot -> slot.get(0) < currentSlotStartAtMillis && currentSlotStartAtMillis < slot.get(1)
                        ||
                        slot.get(0) < currentSlotEndAtMillis && currentSlotEndAtMillis < slot.get(1))
                .toList();

        // throws exception if there is at least one collision
        if (!allCollisionSlots.isEmpty()) {
            throw new TrainingSlotsInCollisionException("Current Training slot in collision with others");
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
        var allTrainingSlots = trainingSlotRepository.findAll();
        if (allTrainingSlots.isEmpty()) {
            throw new EmptyListException(emptyListMessage(SERVICE_NAME));
        } else {
            return ResponseEntity.ok(allTrainingSlots
                    .stream()
                    .map(trainingSlotMapper::toDto)
                    .toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<TrainingSlotDto>> getAllTrainingSlotsByGroupId(Long groupId) {
        var allTrainingSlotsByGroupId = trainingSlotRepository.findByGroupId(groupId);
        if (allTrainingSlotsByGroupId.isEmpty()) {
            throw new EmptyListException("There are no training slots with group id " + groupId);
        }
        return ResponseEntity
                .ok(allTrainingSlotsByGroupId
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
