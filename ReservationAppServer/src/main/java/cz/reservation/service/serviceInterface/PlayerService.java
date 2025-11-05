package cz.reservation.service.serviceInterface;

import cz.reservation.dto.PlayerDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PlayerService {

    PlayerDTO getPlayer(Long id);

    ResponseEntity<PlayerDTO> createPlayer(PlayerDTO playerDTO);

    List<PlayerDTO> getAllPlayers();
}
