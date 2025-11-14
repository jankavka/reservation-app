package cz.reservation.service.serviceinterface;

import cz.reservation.dto.PlayerDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PlayerService {

    PlayerDto getPlayer(Long id);

    ResponseEntity<PlayerDto> createPlayer(PlayerDto playerDTO);

    List<PlayerDto> getAllPlayers();
}
