package cz.reservation.service;

import cz.reservation.constant.Role;
import cz.reservation.dto.UserDto;
import cz.reservation.dto.mapper.UserMapper;
import cz.reservation.entity.filter.UserFilter;
import cz.reservation.entity.repository.specification.UserSpecification;
import cz.reservation.entity.userdetails.CustomUserDetails;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.annotation.ReadOnlyTransaction;
import cz.reservation.service.exception.UnauthorizedEventException;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
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
    private static final String PROTECTED = "===PROTECTED===";

    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(Long id) {
        return userMapper.toDto(userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @ReadOnlyTransaction
    @Override
    public List<UserDto> getAllUsers(UserFilter userFilter) {
        var spec = new UserSpecification(userFilter);
        var userEntities = userRepository.findAll(spec);
        if (userEntities.isEmpty()) {
            log.warn("There are no users in database");
            return List.of();
        }
        return userEntities.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @ReadOnlyTransaction
    @Override
    public User getCurrentUser() {
        Authentication authentication;
        try {
            authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Current user: {}", authentication.getPrincipal());
            var currentUser = (User) authentication.getPrincipal();
            return new User(
                    currentUser.getUsername(),
                    PROTECTED,
                    currentUser.isEnabled(),
                    currentUser.isAccountNonExpired(),
                    currentUser.isCredentialsNonExpired(),
                    currentUser.isAccountNonLocked(),
                    currentUser.getAuthorities());
        } catch (Exception e) {
            log.error("Error: {}, {}", e.getMessage(), e.getClass());
            throw new AuthorizationDeniedException("There is no authenticated user");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void editProfile(UserDto userDto) {
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getUsername().equals(userDto.fullName())) {
            var currentEntity = userRepository
                    .findByEmail(userDto.email())
                    .orElseThrow(() -> new EntityNotFoundException(
                            entityNotFoundExceptionMessage(SERVICE_NAME, userDto.id())));

            userMapper.updateEntity(currentEntity, userDto);

        } else {
            throw new UnauthorizedEventException("User have to rights for this action");
        }
    }

    @Transactional
    @Override
    public void editUserByAdmin(UserDto userDto, Long id) {
        var currentUser = userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));

        userMapper.updateEntity(currentUser, userDto);
    }

    @Override
    public List<UserDto> getAllAdmins() {
        return userRepository.getAllByRoles(Role.ADMIN)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getProfile(String username) {
        return userMapper.toDto(userRepository
                .findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found")));
    }

    @ReadOnlyTransaction
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
