package cz.reservation.service.serviceinterface;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.RegistrationRequestDto;
import cz.reservation.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<LoginResponseDto> authenticate(AuthRequestDTO authRequestDTO);

    ResponseEntity<UserDto> createUser(RegistrationRequestDto registrationRequestDto);
}
