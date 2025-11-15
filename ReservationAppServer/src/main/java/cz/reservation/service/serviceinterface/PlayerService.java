package cz.reservation.service.serviceinterface;

import cz.reservation.dto.PlayerDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PlayerService {

    ResponseEntity<PlayerDto> getPlayer(Long id);

    ResponseEntity<PlayerDto> createPlayer(PlayerDto playerDTO);

    List<PlayerDto> getAllPlayers();

    ResponseEntity<PlayerDto> editPlayer(PlayerDto playerDto, Long id);

    ResponseEntity<HttpStatus> deletePLayer(Long id);
}
