package cz.reservation.service.serviceinterface;

import cz.reservation.dto.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    User getCurrentUser();

    void deleteUser(Long id);

    List<UserDto> getAllAdmins();
}
