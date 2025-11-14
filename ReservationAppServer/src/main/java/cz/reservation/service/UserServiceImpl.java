package cz.reservation.service;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.UserDto;
import cz.reservation.dto.mapper.UserMapper;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.JwtService;
import cz.reservation.service.serviceinterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    @Autowired
    @Lazy
    public UserServiceImpl(
            UserMapper userMapper,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService

    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(Long id) {
        UserDto userDTO = userMapper.toDto(userRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new));

        return new UserDto(
                userDTO.id(),
                userDTO.email(),
                "---protected---",
                userDTO.fullName(),
                userDTO.roles(),
                userDTO.createdAt());

    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        if (userEntities.isEmpty()) {
            log.warn("There are no users in database");
            return List.of();
        }
        return userEntities.stream()
                .map(userMapper::toDto)
                .map(o -> new UserDto(
                        o.id(),
                        o.email(),
                        "---protected--",
                        o.fullName(),
                        o.roles(),
                        o.createdAt()))
                .toList();
    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> createUser(UserDto userDTO) {

        if (userDTO == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        log.info("New user: {}", userDTO);
        UserEntity entityToSave = userMapper.toEntity(userDTO);
        entityToSave.setCreatedAt(new Date());
        entityToSave.setPassword(passwordEncoder.encode(userDTO.password()));
        UserEntity savedEntity = userRepository.save(entityToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userMapper.toDto(savedEntity));


    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<LoginResponseDto> authenticate(AuthRequestDTO authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.username(),
                        authRequestDTO.password())
        );
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(authRequestDTO.username());
            LoginResponseDto responseDto = new LoginResponseDto(
                    token,
                    jwtService.getJwtExpiration());
            return ResponseEntity.ok(responseDto);
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Current user: {}", authentication.getPrincipal());

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);

    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findByEmail(username);

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        UserEntity user = userEntity.get();
        return new User(user.getEmail(), user.getPassword(), user.getAuthorities());

    }
}
