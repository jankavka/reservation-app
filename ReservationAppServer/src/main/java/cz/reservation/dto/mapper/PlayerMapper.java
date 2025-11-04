package cz.reservation.dto.mapper;

import cz.reservation.dto.PlayerDTO;
import cz.reservation.entity.PlayerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerDTO toDTO(PlayerEntity playerEntity);

    PlayerEntity toEntity(PlayerDTO playerDTO);
 }
