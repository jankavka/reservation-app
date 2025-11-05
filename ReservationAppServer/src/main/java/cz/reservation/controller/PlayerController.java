package cz.reservation.controller;

import cz.reservation.dto.PlayerDTO;
import cz.reservation.service.serviceInterface.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService){
        this.playerService = playerService;
    }

    @GetMapping("/player/all")
    public List<PlayerDTO> getAllPlayers(){
        return playerService.getAllPlayers();
    }

    @GetMapping("/player/{id}")
    public PlayerDTO getPlayer(@PathVariable Long id){
        return playerService.getPlayer(id);
    }
}
