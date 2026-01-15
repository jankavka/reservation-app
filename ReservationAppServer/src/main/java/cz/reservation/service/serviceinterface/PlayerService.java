package cz.reservation.service.serviceinterface;

import cz.reservation.dto.PlayerDto;
import cz.reservation.entity.PlayerEntity;

import java.util.List;
import java.util.Map;

public interface PlayerService {

    Map<String, Object> getPlayer(Long id);

    PlayerDto getPlayerDto(Long id);

    PlayerEntity getPlayerEntity(Long id);

    PlayerDto createPlayer(PlayerDto playerDTO);

    List<PlayerDto> getAllPlayers();

    void editPlayer(PlayerDto playerDto, Long id);

    void deletePlayer(Long id);

    List<PlayerDto> getPlayersByParentId(Long id);

    List<PlayerEntity> getPlayersEntitiesByParentId(Long parentId);
}
