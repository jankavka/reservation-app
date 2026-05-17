package cz.reservation.controller;

import cz.reservation.dto.*;
import cz.reservation.service.serviceinterface.AuthService;
import cz.reservation.service.serviceinterface.JwtService;
import cz.reservation.service.serviceinterface.RefreshTokenService;
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

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;


    @PostMapping("/addNewUser")
    public UserDto createUser(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {
        return authService.createUser(registrationRequestDto);
    }

    @PostMapping("/generateToken")
    public LoginResponseDto authenticateAndGetToken(
            @RequestBody AuthRequestDTO authRequestDTO) {
        return authService.authenticate(authRequestDTO);
    }

    @PostMapping("/refresh")
    public LoginResponseDto refresh(@RequestBody RefreshTokenRequestDto refreshTokenRequest){
        return authService.refresh(refreshTokenRequest);


    }

}
