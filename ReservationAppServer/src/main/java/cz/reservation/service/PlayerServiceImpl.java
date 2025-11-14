package cz.reservation.service;

import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.mapper.PlayerMapper;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.PlayerService;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public PlayerDto getPlayer(Long id) {
        return playerMapper.toDto(playerRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    @Transactional
    public ResponseEntity<PlayerDto> createPlayer(PlayerDto playerDTO) {
        User currentUser = userService.getCurrentUser().getBody();
        if (playerDTO == null) {
            throw new EntityNotFoundException("Player must not be null");
        }
        else if (currentUser == null) {
            throw new EntityNotFoundException("Entity of parent or admin not found");
        } else {
            String authorities = currentUser.getAuthorities().stream()
                    .map(String::valueOf)
                    .toList()
                    .get(0);

            PlayerEntity entityToSave;
            PlayerEntity savedEntity = new PlayerEntity();

            if (authorities.contains("ADMIN")) {
                entityToSave = playerMapper.toEntity(playerDTO);
                savedEntity = playerRepository.save(entityToSave);


            } else if (authorities.contains("PARENT")) {
                entityToSave = playerMapper.toEntity(playerDTO);
                entityToSave.setParent(userRepository.findByEmail(currentUser
                                .getUsername())
                        .orElseThrow(EntityNotFoundException::new));
                savedEntity = playerRepository.save(entityToSave);

            }

            return ResponseEntity.ok(playerMapper.toDto(savedEntity));
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
}
