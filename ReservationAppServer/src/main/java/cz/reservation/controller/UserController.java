package cz.reservation.controller;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.UserDTO;
import cz.reservation.service.serviceInterface.JwtService;
import cz.reservation.service.serviceInterface.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/user/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/user/all")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/addNewUser")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.username(), authRequestDTO.password())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequestDTO.username());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }


}
