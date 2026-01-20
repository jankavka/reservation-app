package cz.reservation.controller;

import cz.reservation.dto.GroupDto;
import cz.reservation.entity.filter.GroupFilter;
import cz.reservation.service.serviceinterface.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDto createGroup(@RequestBody @Valid GroupDto groupDto) {
        return groupService.createGroup(groupDto);
    }

    @GetMapping("/{id}")
    public GroupDto getGroup(@PathVariable Long id) {
        return groupService.getGroup(id);
    }

    @GetMapping
    public List<GroupDto> getAllGroups(GroupFilter groupFilter) {
        return groupService.getAllGroups(groupFilter);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editGroup(@RequestBody @Valid GroupDto groupDto, @PathVariable Long id) {
        groupService.editGroup(groupDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
    }
}
