package cz.reservation.controller;

import cz.reservation.dto.PlayerDto;
import cz.reservation.service.serviceinterface.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService){
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerDto> getAllPlayers(){
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    public PlayerDto getPlayer(@PathVariable Long id){
        return playerService.getPlayer(id);
    }

    @PostMapping
    public ResponseEntity<PlayerDto> createPlayer(@RequestBody @Valid PlayerDto playerDto){
        return playerService.createPlayer(playerDto);
    }
}
