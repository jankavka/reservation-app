package cz.reservation.dto.mapper;

import cz.reservation.dto.PlayerDto;
import cz.reservation.entity.PlayerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class, PackageMapper.class})
public interface PlayerMapper {


    PlayerDto toDto(PlayerEntity playerEntity);

    PlayerEntity toEntity(PlayerDto playerDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget PlayerEntity target, PlayerDto source);
 }
