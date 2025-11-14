package cz.reservation.service;

import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.mapper.PlayerMapper;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.service.serviceinterface.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    @Autowired
    public PlayerServiceImpl(PlayerMapper playerMapper, PlayerRepository playerRepository) {
        this.playerMapper = playerMapper;
        this.playerRepository = playerRepository;

    }

    @Override
    public PlayerDto getPlayer(Long id) {
        return playerMapper.toDto(playerRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public ResponseEntity<PlayerDto> createPlayer(PlayerDto playerDTO) {
        return null;
    }

    @Override
    public List<PlayerDto> getAllPlayers() {

        List<PlayerEntity> playerEntities = playerRepository.findAll();
        if(playerEntities.isEmpty()){
            log.warn("There are no players in database");
            return List.of();
        }
        return playerEntities.stream().map(playerMapper::toDto).toList();


    }
}
