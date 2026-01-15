package cz.reservation.controller;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.RegistrationRequestDto;
import cz.reservation.dto.UserDto;
import cz.reservation.service.serviceinterface.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/addNewUser")
    public UserDto createUser(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {
        return authService.createUser(registrationRequestDto);
    }

    @PostMapping("/generateToken")
    public LoginResponseDto authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        return authService.authenticate(authRequestDTO);
    }

}
