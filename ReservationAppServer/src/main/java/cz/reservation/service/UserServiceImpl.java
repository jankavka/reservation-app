package cz.reservation.service;

import cz.reservation.dto.UserDTO;
import cz.reservation.dto.mapper.UserMapper;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.UserEntityDetails;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceInterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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



    @Autowired
    @Lazy
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUser(Long id) {
        return userMapper.toDTO(userRepository.findById(id).orElseThrow(EntityNotFoundException::new));

    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDTO> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        if(userEntities.isEmpty()){
            log.warn("There are no users in database");
            return List.of();
        }
        return userEntities.stream().map(userMapper::toDTO).toList();
    }

    @Transactional
    @Override
    public ResponseEntity<UserDTO> createUser(UserDTO userDTO) {

        if (userDTO == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        System.out.println("New User: " + userDTO);
        UserEntity entityToSave = userMapper.toEntity(userDTO);
        entityToSave.setCreatedAt(new Date());
        entityToSave.setPassword(passwordEncoder.encode(userDTO.password()));
        UserEntity savedEntity = userRepository.save(entityToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userMapper.toDTO(savedEntity));


    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findByEmail(username);

        if(userEntity.isEmpty()){
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        UserEntity user = userEntity.get();
        UserEntityDetails details = new UserEntityDetails(user);
        return new User(user.getEmail(), user.getPassword(), details.getAuthorities());

    }
}
