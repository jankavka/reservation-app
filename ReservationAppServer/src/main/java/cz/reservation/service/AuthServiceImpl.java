package cz.reservation.service;

import cz.reservation.dto.*;
import cz.reservation.dto.mapper.UserMapper;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.AuthService;
import cz.reservation.service.serviceinterface.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher publisher;


    @Override
    public ResponseEntity<LoginResponseDto> authenticate(AuthRequestDTO authRequestDTO) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.username(),
                        authRequestDTO.password())
        );
        if (authentication.isAuthenticated()) {
            var token = jwtService.generateToken(authRequestDTO.username());
            var responseDto = new LoginResponseDto(
                    token,
                    jwtService.getJwtExpiration());
            return ResponseEntity.ok(responseDto);
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<UserDto> createUser(RegistrationRequestDto registrationRequestDto) {
        log.info("New user: {}", registrationRequestDto);

        var entityToSave = UserEntity.builder()
                .email(registrationRequestDto.email())
                .fullName(registrationRequestDto.fullName())
                .roles(registrationRequestDto.roles())
                .createdAt(LocalDateTime.now())
                .password(passwordEncoder.encode(registrationRequestDto.password()))
                .telephoneNumber(registrationRequestDto.telephoneNumber())
                .build();

        UserEntity savedEntity = userRepository.save(entityToSave);

        publisher.publishEvent(new CreatedUserDto(this, savedEntity));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userMapper.toDto(savedEntity));


    }

}
