package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.dto.CourtDto;
import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.dto.mapper.TrainingSlotMapper;
import cz.reservation.entity.repository.CourtBlockingRepository;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.TrainingSlotRepository;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.exception.TrainingSlotsInCollisionException;
import cz.reservation.service.serviceinterface.CourtBlockingService;
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

    private final CourtBlockingRepository courtBlockingRepository;

    private final CourtBlockingService courtBlockingService;

    private static final String SERVICE_NAME = "training slot";

    public TrainingSlotServiceImpl(
            TrainingSlotMapper trainingSlotMapper,
            TrainingSlotRepository trainingSlotRepository,
            CourtRepository courtRepository,
            GroupRepository groupRepository,
            CourtBlockingRepository courtBlockingRepository,
            CourtBlockingService courtBlockingService
    ) {
        this.trainingSlotMapper = trainingSlotMapper;
        this.trainingSlotRepository = trainingSlotRepository;
        this.courtRepository = courtRepository;
        this.groupRepository = groupRepository;
        this.courtBlockingRepository = courtBlockingRepository;
        this.courtBlockingService = courtBlockingService;

    }

    /**
     * Method creates new training slot in database if there are existing related court and group and
     * there are no time collisions between current slot and all already existing court blockings.
     * Otherwise, it throws EntityNoFindException for missing court or group or TrainingSlotsInCollisionException
     * if there are court blockings in time collision with the current one.
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

        var allBlockings = courtBlockingRepository.findAll();
        var currentSlotStartAtMillis = trainingSlotDto.startAt().toEpochSecond(ZoneOffset.UTC);
        var currentSlotEndAtMillis = trainingSlotDto.endAt().toEpochSecond(ZoneOffset.UTC);
        var groupName = groupRepository.getReferenceById(groupId).getName();

        //looking for collision between current slot and all blockings of current court
        var allCollisionBlockings = allBlockings
                .stream()
                //finds blockings with corresponding court
                .filter(blocking -> blocking.getCourt().getId().equals(courtId))
                .map(blocking -> List.of(
                        blocking.getBlockedFrom().toEpochSecond(ZoneOffset.UTC),
                        blocking.getBlockedTo().toEpochSecond(ZoneOffset.UTC)))
                //finds all blockings, which are in collision with the current training slot
                .filter(blocking ->
                        blocking.get(0) < currentSlotStartAtMillis && currentSlotStartAtMillis < blocking.get(1)
                                ||
                                blocking.get(0) < currentSlotEndAtMillis && currentSlotEndAtMillis < blocking.get(1))
                .toList();

        //throws exception if there is at least one collision
        if (!allCollisionBlockings.isEmpty()) {
            throw new TrainingSlotsInCollisionException("Current Training slot in collision with court blockings");
        }

        //related blocking of court saved together with the slot
        var relatedCourtBlocking = new CourtBlockingDto(
                null,
                new CourtDto(
                        courtId,
                        null,
                        null,
                        null,
                        null,
                        null),
                trainingSlotDto.startAt(),
                trainingSlotDto.endAt(),
                SERVICE_NAME + " of group " + groupName + " (id = " + groupId + ")");


        courtBlockingService.createBlocking(relatedCourtBlocking);
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
