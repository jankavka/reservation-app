package cz.reservation.service.serviceinterface;

import cz.reservation.dto.GroupDto;

import java.util.List;

public interface GroupService {

    GroupDto createGroup(GroupDto groupDto);

    GroupDto getGroup(Long id);

    List<GroupDto> getAllGroups();

    void editGroup(GroupDto groupDto, Long id);

    void deleteGroup(Long id);
}
