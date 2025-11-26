package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.Role;
import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.mapper.PlayerMapper;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;


@Service
@Slf4j
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    private final UserRepository userRepository;

    private static final String SERVICE_NAME = "player";

    private static final String ID = "id";

    public PlayerServiceImpl(
            PlayerMapper playerMapper,
            PlayerRepository playerRepository,
            UserRepository userRepository) {
        this.playerMapper = playerMapper;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<PlayerDto> getPlayer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(notNullMessage(ID));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(playerMapper.toDto(playerRepository
                            .findById(id)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    entityNotFoundExceptionMessage(SERVICE_NAME, id)))));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<PlayerDto> createPlayer(PlayerDto playerDTO) {
        if (playerDTO == null) {
            throw new IllegalArgumentException(notNullMessage(SERVICE_NAME));
        } else {
            var entityToSave = playerMapper.toEntity(playerDTO);
            Long parentId = playerDTO.parent().id();
            if (userRepository.existsById(parentId)) {
                userRepository.getReferenceById(parentId).getRoles().add(Role.PARENT);
                entityToSave.setParent(userRepository
                        .findById(parentId)
                        .orElseThrow());

                var savedEntity = playerRepository.save(entityToSave);

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(playerMapper.toDto(savedEntity));
            } else {
                throw new EntityNotFoundException(entityNotFoundExceptionMessage("parent user", parentId));
            }


        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerDto> getAllPlayers() {
        var playerEntities = playerRepository.findAll();
        if (playerEntities.isEmpty()) {
            log.warn("There are no players in database");
            return List.of();
        }
        return playerEntities.stream().map(playerMapper::toDto).toList();

    }

    @Override
    @Transactional
    public ResponseEntity<PlayerDto> editPlayer(PlayerDto playerDto, Long id) {
        if (id == null) {
            throw new IllegalArgumentException(notNullMessage(ID));
        } else if (playerRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));

        } else {
            var entityToEdit = playerMapper.toEntity(playerDto);
            entityToEdit.setId(id);
            var savedEntity = playerRepository.save(entityToEdit);

            return ResponseEntity.ok(playerMapper.toDto(savedEntity));

        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deletePLayer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(notNullMessage(ID));
        } else if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));

        } else {
            playerRepository.deleteById(id);


            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<PlayerDto>> getPlayersByParentId(Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playerRepository.findByParentId(id)
                        .stream()
                        .map(playerMapper::toDto)
                        .toList());
    }
}
