package cz.reservation.controller;

import cz.reservation.dto.UserDto;
import cz.reservation.entity.filter.UserFilter;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public List<UserDto> getAllUsers(UserFilter userFilter) {
        return userService.getAllUsers(userFilter);
    }


    @GetMapping("/current")
    public User showCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/profile/{username}")
    public UserDto getProfile(@PathVariable("username") String username) {
        return userService.getProfile(username);
    }

    @GetMapping("/all-admins")
    public List<UserDto> getAllAdmins() {
        return userService.getAllAdmins();
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest req) throws ServletException {
        req.logout();
        SecurityContextHolder.getContext().setAuthentication(null);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out");
        return ResponseEntity.ok(response);

    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/profile/{id}")
    public void updateProfile(@RequestBody UserDto userDto) {
        userService.editProfile(userDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public void updateUserByAdmin(@RequestBody UserDto userDto, @PathVariable Long id) {
        userService.editUserByAdmin(userDto, id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }


}
