package cz.reservation.service.serviceinterface;

import cz.reservation.dto.PlayerDto;
import cz.reservation.entity.PlayerEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PlayerService {

    ResponseEntity<PlayerDto> getPlayer(Long id);

    ResponseEntity<PlayerDto> createPlayer(PlayerDto playerDTO);

    List<PlayerDto> getAllPlayers();

    ResponseEntity<PlayerDto> editPlayer(PlayerDto playerDto, Long id);

    ResponseEntity<Map<String, String>> deletePLayer(Long id);

    ResponseEntity<List<PlayerDto>> getPlayersByParentId(Long id);

    List<PlayerEntity> getPlayersEntitiesByParentId(Long parentId);
}
