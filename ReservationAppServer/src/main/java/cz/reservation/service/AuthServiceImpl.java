package cz.reservation.service;

import cz.reservation.dto.AuthRequestDTO;
import cz.reservation.dto.LoginResponseDto;
import cz.reservation.dto.RegistrationRequestDto;
import cz.reservation.dto.UserDto;
import cz.reservation.dto.mapper.UserMapper;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.AuthService;
import cz.reservation.service.serviceinterface.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        var entityToSave = new UserEntity();
        entityToSave.setEmail(registrationRequestDto.email());
        entityToSave.setFullName(registrationRequestDto.fullName());
        entityToSave.setRoles(registrationRequestDto.roles());
        entityToSave.setCreatedAt(LocalDateTime.now());
        entityToSave.setPassword(passwordEncoder.encode(registrationRequestDto.password()));
        entityToSave.setTelephoneNumber(registrationRequestDto.telephoneNumber());
        UserEntity savedEntity = userRepository.save(entityToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userMapper.toDto(savedEntity));


    }

}
