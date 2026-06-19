package cz.reservation.dto.mapper;

import cz.reservation.dto.UserDto;
import cz.reservation.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(UserEntity userEntity);


    UserEntity toEntity(UserDto toDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget UserEntity target, UserDto source);
}
