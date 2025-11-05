package cz.reservation.dto.mapper;

import cz.reservation.dto.UserDTO;
import cz.reservation.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserEntity userEntity);

    UserEntity toEntity(UserDTO toDTO);
}
