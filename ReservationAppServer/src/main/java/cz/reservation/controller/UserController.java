package cz.reservation.controller;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.RegistrationRequestDto;
import cz.reservation.dto.UserDto;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/user/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/user/all")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/auth/addNewUser")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {
        return userService.createUser(registrationRequestDto);
    }

    @PostMapping("/auth/generateToken")
    public ResponseEntity<LoginResponseDto> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        return userService.authenticate(authRequestDTO);
    }

    @GetMapping("/user/current")
    public ResponseEntity<User> showCurrentUser(){
        return userService.getCurrentUser();
    }

    @DeleteMapping("/user/logout")
    public ResponseEntity<Map<String,String>> logout(HttpServletRequest req) throws ServletException {
        req.logout();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out");
        return ResponseEntity.ok(response);

    }


}
