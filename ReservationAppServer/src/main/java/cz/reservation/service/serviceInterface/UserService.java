package cz.reservation.service.serviceInterface;

import cz.reservation.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDTO getUser(Long id);

    List<UserDTO> getAllUsers();

    ResponseEntity<UserDTO> createUser(UserDTO userDTO);
}
