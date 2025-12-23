package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.UserDto;
import cz.reservation.dto.mapper.UserMapper;
import cz.reservation.entity.userdetails.CustomUserDetails;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private static final String SERVICE_NAME = "user";


    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(Long id) {
        return userMapper.toDto(userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                entityNotFoundExceptionMessage(SERVICE_NAME, id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        var userEntities = userRepository.findAll();
        if (userEntities.isEmpty()) {
            log.warn("There are no users in database");
            return List.of();
        }
        return userEntities.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<User> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Current user: {}", authentication.getPrincipal());

        var currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);

    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var userEntity = userRepository.findByEmail(username);

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        var user = userEntity.get();
        var customUserDetails = new CustomUserDetails(user);
        return new User(user.getEmail(), user.getPassword(), customUserDetails.getAuthorities());

    }
}
