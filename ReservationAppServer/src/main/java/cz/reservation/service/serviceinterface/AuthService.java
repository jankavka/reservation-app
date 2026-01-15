package cz.reservation.service.serviceinterface;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.RegistrationRequestDto;
import cz.reservation.dto.UserDto;

public interface AuthService {

    LoginResponseDto authenticate(AuthRequestDTO authRequestDTO);

    UserDto createUser(RegistrationRequestDto registrationRequestDto);
}
