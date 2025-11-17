package cz.reservation.service;

import cz.reservation.constant.Role;
import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.RegistrationRequestDto;
import cz.reservation.dto.mapper.PlayerMapper;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.PlayerService;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    private final UserService userService;

    private final UserRepository userRepository;

    @Autowired
    public PlayerServiceImpl(
            PlayerMapper playerMapper,
            PlayerRepository playerRepository,
            UserService userService,
            UserRepository userRepository) {
        this.playerMapper = playerMapper;
        this.playerRepository = playerRepository;
        this.userService = userService;
        this.userRepository = userRepository;

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<PlayerDto> getPlayer(Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playerMapper.toDto(playerRepository
                        .findById(id)
                        .orElseThrow(EntityNotFoundException::new)));
    }

    @Override
    @Transactional
    public ResponseEntity<PlayerDto> createPlayer(PlayerDto playerDTO) {
        User currentUser = userService.getCurrentUser().getBody();
        if (playerDTO != null && currentUser != null) {
            PlayerEntity entityToSave;
            entityToSave = playerMapper.toEntity(playerDTO);
            Long parentId = playerDTO.parent().id();
            userRepository.getReferenceById(parentId).getRoles().add(Role.PARENT);
            entityToSave.setParent(userRepository
                    .findById(playerDTO.parent().id())
                    .orElseThrow(EntityNotFoundException::new));

            PlayerEntity savedEntity = playerRepository.save(entityToSave);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(playerMapper.toDto(savedEntity));

        } else {
            if (playerDTO == null) {
                throw new IllegalArgumentException("Player can not be null");

            } else {
                throw new IllegalArgumentException("Current user can not be null");
            }
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerDto> getAllPlayers() {
        List<PlayerEntity> playerEntities = playerRepository.findAll();
        if (playerEntities.isEmpty()) {
            log.warn("There are no players in database");
            return List.of();
        }
        return playerEntities.stream().map(playerMapper::toDto).toList();

    }

    @Override
    @Transactional
    public ResponseEntity<PlayerDto> editPlayer(PlayerDto playerDto, Long id) {
        if (playerRepository.existsById(id)) {
            PlayerEntity entityToEdit = playerMapper.toEntity(playerDto);
            entityToEdit.setId(id);
            PlayerEntity savedEntity = playerRepository.save(entityToEdit);

            return ResponseEntity.ok(playerMapper.toDto(savedEntity));

        } else {
            throw new EntityNotFoundException("Player not found");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> deletePLayer(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("Player not found");
        }
    }
}
