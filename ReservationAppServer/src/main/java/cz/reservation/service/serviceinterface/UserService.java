package cz.reservation.service.serviceinterface;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.RegistrationRequestDto;
import cz.reservation.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {

    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    ResponseEntity<UserDto> createUser(RegistrationRequestDto registrationRequestDto);

    ResponseEntity<LoginResponseDto> authenticate(AuthRequestDTO authRequestDTO);

    ResponseEntity<User> getCurrentUser();

    ResponseEntity<Map<String, String>> deleteUser(Long id);
}
