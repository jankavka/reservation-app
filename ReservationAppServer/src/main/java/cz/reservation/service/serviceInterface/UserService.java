package cz.reservation.service.serviceInterface;

import cz.reservation.dto.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    UserDTO getUser(Long id);

    List<UserDTO> getAllUsers();

    ResponseEntity<UserDTO> createUser(UserDTO userDTO);
}
