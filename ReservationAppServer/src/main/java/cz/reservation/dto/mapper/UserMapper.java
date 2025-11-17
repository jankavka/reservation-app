package cz.reservation.dto.mapper;

import cz.reservation.dto.UserDto;
import cz.reservation.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "players", ignore = true)
    UserDto toDto(UserEntity userEntity);


    UserEntity toEntity(UserDto toDTO);
}
