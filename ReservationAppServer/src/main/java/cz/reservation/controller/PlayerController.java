package cz.reservation.controller;

import cz.reservation.dto.PlayerDto;
import cz.reservation.service.serviceinterface.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public List<PlayerDto> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getPlayer(@PathVariable Long id) {
        return playerService.getPlayer(id);
    }

    @GetMapping("/user/{id}")
    public List<PlayerDto> getPlayerByParentId(@PathVariable Long id) {
        return playerService.getPlayersByParentId(id);
    }

    @Secured("ADMIN")
    @PostMapping
    public ResponseEntity<PlayerDto> createPlayer(@RequestBody @Valid PlayerDto playerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(playerDto));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editPlayer(@RequestBody @Valid PlayerDto playerDto, @PathVariable Long id) {
        playerService.editPlayer(playerDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }
}
