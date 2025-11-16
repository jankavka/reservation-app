package cz.reservation.dto.mapper;

import cz.reservation.dto.GroupDto;
import cz.reservation.entity.GroupEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupEntity toEntity(GroupDto groupDto);

    GroupDto toDto(GroupEntity groupEntity);
}
