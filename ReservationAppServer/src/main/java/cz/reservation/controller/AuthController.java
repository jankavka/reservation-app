package cz.reservation.controller;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.RegistrationRequestDto;
import cz.reservation.dto.UserDto;
import cz.reservation.entity.RefreshToken;
import cz.reservation.service.serviceinterface.AuthService;
import cz.reservation.service.serviceinterface.JwtService;
import cz.reservation.service.serviceinterface.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


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
            @RequestBody AuthRequestDTO authRequestDTO,
            HttpServletRequest req,
            HttpServletResponse res) {

        return authService.authenticate(authRequestDTO);
    }

}
