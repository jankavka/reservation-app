package cz.reservation.dto.mapper;

import cz.reservation.dto.GroupDto;
import cz.reservation.entity.GroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupEntity toEntity(GroupDto groupDto);

    GroupDto toDto(GroupEntity groupEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    @Mapping(target = "coach", ignore = true)
    void updateEntity(@MappingTarget GroupEntity target, GroupDto source);
}
