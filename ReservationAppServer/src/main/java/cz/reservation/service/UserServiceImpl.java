package cz.reservation.service;

import cz.reservation.constant.Role;
import cz.reservation.dto.UserDto;
import cz.reservation.dto.mapper.UserMapper;
import cz.reservation.entity.userdetails.CustomUserDetails;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

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
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current user: {}", authentication.getPrincipal());
        return (User) authentication.getPrincipal();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAllAdmins() {
        return userRepository.getAllByRoles(Role.ADMIN)
                .stream()
                .map(userMapper::toDto)
                .toList();
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
