package cz.reservation.service.serviceinterface;

import cz.reservation.dto.GroupDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GroupService {

    ResponseEntity<GroupDto> createGroup(GroupDto groupDto);

    ResponseEntity<GroupDto> getGroup(Long id);

    ResponseEntity<List<GroupDto>> getAllGroups();

    ResponseEntity<GroupDto> editGroup(GroupDto groupDto, Long id);

    ResponseEntity<HttpStatus> deleteGroup(Long id);
}
