package cz.reservation.dto.mapper;

import cz.reservation.dto.UserDto;
import cz.reservation.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(UserEntity userEntity);

    UserEntity toEntity(UserDto toDTO);
}
