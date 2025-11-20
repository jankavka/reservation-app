package cz.reservation.controller;

import cz.reservation.dto.GroupDto;
import cz.reservation.service.serviceinterface.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService){
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody @Valid GroupDto groupDto){
        return groupService.createGroup(groupDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable Long id){
        return groupService.getGroup(id);
    }

    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups(){
        return groupService.getAllGroups();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> editGroup(
            @RequestBody @Valid GroupDto groupDto,
            @PathVariable Long id){

        return groupService.editGroup(groupDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGroup(@PathVariable Long id){
        return groupService.deleteGroup(id);
    }
}
