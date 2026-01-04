package cz.reservation.controller;

import cz.reservation.constant.Role;
import cz.reservation.dto.PlayerDto;
import cz.reservation.service.serviceinterface.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerDto> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPlayer(@PathVariable Long id) {
        return playerService.getPlayer(id);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<PlayerDto>> getPlayerByParentId(@PathVariable Long id) {
        return playerService.getPlayersByParentId(id);
    }

    @Secured("ADMIN")
    @PostMapping
    public ResponseEntity<PlayerDto> createPlayer(@RequestBody @Valid PlayerDto playerDto) {
        return playerService.createPlayer(playerDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editPlayer(
            @RequestBody @Valid PlayerDto playerDto,
            @PathVariable Long id) {
        return playerService.editPlayer(playerDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePlayer(@PathVariable Long id) {
        return playerService.deletePLayer(id);
    }
}
