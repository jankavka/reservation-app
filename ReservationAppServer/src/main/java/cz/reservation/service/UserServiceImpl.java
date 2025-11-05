package cz.reservation.service;

import cz.reservation.dto.UserDTO;
import cz.reservation.service.serviceInterface.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {




    @Override
    public UserDTO getUser(Long id) {
        return null;
    }

    @Override
    public List<UserDTO> getAllUsers(Long id) {
        return List.of();
    }

    @Override
    public ResponseEntity<UserDTO> createPlayer(UserDTO userDTO) {
        return null;
    }
}
