package cz.reservation.service.serviceInterface;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDTO getUser(Long id);

    List<UserDTO> getAllUsers();

    ResponseEntity<UserDTO> createUser(UserDTO userDTO);

    ResponseEntity<LoginResponseDto> authenticate(AuthRequestDTO authRequestDTO);

    ResponseEntity<User> getCurrentUser();
}
