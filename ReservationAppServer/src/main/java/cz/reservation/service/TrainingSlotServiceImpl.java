package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.CourtBlockingDto;
import cz.reservation.dto.CourtDto;
import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.dto.mapper.TrainingSlotMapper;
import cz.reservation.entity.CourtBlockingEntity;
import cz.reservation.entity.TrainingSlotEntity;
import cz.reservation.entity.repository.CourtRepository;
import cz.reservation.entity.repository.GroupRepository;
import cz.reservation.entity.repository.TrainingSlotRepository;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.exception.TrainingSlotsInCollisionException;
import cz.reservation.service.serviceinterface.CourtBlockingService;
import cz.reservation.service.serviceinterface.TrainingSlotService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class TrainingSlotServiceImpl implements TrainingSlotService {

    private final TrainingSlotRepository trainingSlotRepository;

    private final TrainingSlotMapper trainingSlotMapper;

    private final CourtRepository courtRepository;

    private final GroupRepository groupRepository;

    private final CourtBlockingService courtBlockingService;

    private static final String SERVICE_NAME = "training slot";


    /**
     * Method creates new training slot in database if there are existing related court and group and
     * there are no time collisions between current slot and all already existing court blockings.
     * Otherwise, it throws EntityNoFindException for missing court or group or TrainingSlotsInCollisionException
     * if there are court blockings in time collision with the current one. If there is no exception thrown
     * training slot and related court blocking is created.
     *
     * @param trainingSlotDto data object with new training slot
     * @return ResponseEntity with status CREATED and created object as an instance of TrainingSlotDto
     */
    @Override
    @Transactional
    public ResponseEntity<TrainingSlotDto> createTrainingSlot(TrainingSlotDto trainingSlotDto) {

        var entityToSave = trainingSlotMapper.toEntity(trainingSlotDto);

        //Checking existing related group
        setForeignKeys(entityToSave, trainingSlotDto);

        var allBlockings = courtBlockingService.getAllBlockingsEntities();

        //Throws exception if there is at least one collision
        isThereTimeCollision(
                allBlockings,
                null,
                trainingSlotDto
        );

        //Related blocking of court saved together with the slot
        var relatedCourtBlockingDto = createRelatedCourtBlockingDto(trainingSlotDto);

        //Sets related court blocking as FK to entityToSave
        setRelatedBlockIfItsNotNull(relatedCourtBlockingDto, entityToSave);

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
    public TrainingSlotEntity getTrainingSlotEntity(Long id) {
        return trainingSlotRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id)));

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

    /**
     * Method updates existing training slot. Contains check if current slot exists, otherwise
     * EntityNotFoundException is thrown. Also contains check if there is not a time collision
     * between current slot and other court blockings, otherwise TrainingSlotsInCollisionException
     * is thrown. If there is no exception thrown training slot and related court blocking is updated.
     *
     * @param trainingSlotDto Object with updated attributes
     * @param id              PK of training slot in database
     * @return Response entity with success message
     */
    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editTrainingSlot(TrainingSlotDto trainingSlotDto, Long id) {
        if (!trainingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {

            var entityToUpdate = trainingSlotRepository.getReferenceById(id);

            var allBlockings = courtBlockingService.getAllBlockingsEntities();

            //Object to use in collision check
            var relatedCourtBlockingEntity = courtBlockingService.getBlockingEntity(trainingSlotDto.courtBlockingId());

            //Collision check. looking for collision between current slot and all blockings of current court
            isThereTimeCollision(
                    allBlockings,
                    relatedCourtBlockingEntity,
                    trainingSlotDto);


            //Creating a new court blocking object for editing the current related one
            var editedRelatedCourtBlockingDto = new CourtBlockingDto(
                    relatedCourtBlockingEntity.getId(),
                    trainingSlotDto.court(),
                    trainingSlotDto.startAt(),
                    trainingSlotDto.endAt(),
                    relatedCourtBlockingEntity.getReason());

            //Editing related court blocking
            courtBlockingService.editBlocking(editedRelatedCourtBlockingDto, relatedCourtBlockingEntity.getId());

            //Editing current training slot
            trainingSlotMapper.updateEntity(entityToUpdate, trainingSlotDto);

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

    /**
     * Computes used capacity of training slot represented by its ID
     *
     * @param id param of related training slot
     * @return Integer representation of used capacity in current training slot
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getUsedCapacityOfRelatedTrainingSlot(Long id) {
        var currentSlot = trainingSlotRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        return currentSlot.getBookingEntities().size();

    }


    /**
     * Helper method. Looking for collision between current slot and all blockings of current court
     *
     * @param allBlockings               All court blocking entities, which are present in db
     * @param relatedCourtBlockingEntity entity of related court
     * @param trainingSlotDto            object with current training slot data
     */
    private void isThereTimeCollision(
            List<CourtBlockingEntity> allBlockings,
            @Nullable CourtBlockingEntity relatedCourtBlockingEntity,
            TrainingSlotDto trainingSlotDto

    ) {

        var currentSlotStartAtMillis = trainingSlotDto.startAt().toEpochSecond(ZoneOffset.UTC);
        var currentSlotEndAtMillis = trainingSlotDto.endAt().toEpochSecond(ZoneOffset.UTC);
        var courtId = trainingSlotDto.court().id();

        var areBlockingsEmpty = allBlockings
                .stream()
                //removes current blocking from the list, if not present returns true
                .filter(blocking -> {
                    if (relatedCourtBlockingEntity != null) {
                        return !blocking.getId().equals(relatedCourtBlockingEntity.getId());
                    } else {
                        return true;
                    }
                })
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
                .toList()
                .isEmpty();

        if (!areBlockingsEmpty) {
            throw new TrainingSlotsInCollisionException(
                    "Current Training slot is in collision with court blockings");
        }
    }

    /**
     * Helper method. Sets foreign keys for related group and court
     *
     * @param entityToSave    TrainingSlotEntity object which is preparing for persisting
     * @param trainingSlotDto data transfer object with id's of related group and court
     */
    private void setForeignKeys(TrainingSlotEntity entityToSave, TrainingSlotDto trainingSlotDto) {
        var groupId = trainingSlotDto.group().id();
        var courtId = trainingSlotDto.court().id();

        //Checking existing related group
        if (groupRepository.existsById(groupId)) {
            entityToSave.setGroup(groupRepository.getReferenceById(trainingSlotDto.group().id()));

            //setting the same capacity as related group has
            entityToSave.setCapacity(entityToSave.getGroup().getCapacity());
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(
                    "group", trainingSlotDto.group().id()));
        }

        //Checking existing related court
        if (courtRepository.existsById(courtId)) {
            entityToSave.setCourt(courtRepository.getReferenceById(courtId));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("court", courtId));
        }
    }

    /**
     * If related "relatedCourtBlockingDto" is not null than it is set as FK, otherwise NullPointerException is
     * thrown
     *
     * @param relatedCourtBlockingDto data transfer object with related court blocking data
     * @param entityToSave            TrainingSlotEntity object which is preparing for persisting
     */
    private void setRelatedBlockIfItsNotNull(CourtBlockingDto relatedCourtBlockingDto, TrainingSlotEntity entityToSave) {
        //Creating related court blocking for later use
        var relatedCourtBlocking = courtBlockingService.createBlocking(relatedCourtBlockingDto).getBody();

        //If related court blocking is not null the blocking is set up as FK to current training slot
        if (relatedCourtBlocking != null) {
            entityToSave.setCourtBlocking(courtBlockingService.getBlockingEntity(relatedCourtBlocking.id()));
        } else {
            throw new NullPointerException(
                    "Error during setting related court blocking. Court blocking must not be null");
        }
    }

    /**
     * Creates an CourtBlockingDto which is related to current training slot.
     *
     * @param trainingSlotDto data transfer object which is base for creating CourtBlockingDto object
     * @return CourtBlockingDto object related to current training slot
     */
    private CourtBlockingDto createRelatedCourtBlockingDto(TrainingSlotDto trainingSlotDto) {
        var groupId = trainingSlotDto.group().id();
        var groupName = groupRepository.getReferenceById(groupId).getName();
        var courtId = trainingSlotDto.court().id();


        return new CourtBlockingDto(
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
    }
}