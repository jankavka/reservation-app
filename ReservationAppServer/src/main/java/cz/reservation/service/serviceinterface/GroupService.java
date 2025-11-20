package cz.reservation.service.serviceinterface;

import cz.reservation.dto.GroupDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface GroupService {

    ResponseEntity<GroupDto> createGroup(GroupDto groupDto);

    ResponseEntity<GroupDto> getGroup(Long id);

    ResponseEntity<List<GroupDto>> getAllGroups();

    ResponseEntity<GroupDto> editGroup(GroupDto groupDto, Long id);

    ResponseEntity<Map<String, String>> deleteGroup(Long id);
}
