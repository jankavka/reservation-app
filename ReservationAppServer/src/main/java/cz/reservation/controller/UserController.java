package cz.reservation.controller;

import cz.reservation.dto.UserDto;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/current")
    public User showCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/all-admins")
    public List<UserDto> getAllAdmins() {
        return userService.getAllAdmins();
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest req) throws ServletException {
        req.logout();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out");
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }


}
