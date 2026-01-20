package cz.reservation.service.serviceinterface;

import cz.reservation.dto.GroupDto;
import cz.reservation.entity.filter.GroupFilter;

import java.util.List;

public interface GroupService {

    GroupDto createGroup(GroupDto groupDto);

    GroupDto getGroup(Long id);

    List<GroupDto> getAllGroups(GroupFilter groupFilter);

    void editGroup(GroupDto groupDto, Long id);

    void deleteGroup(Long id);
}
