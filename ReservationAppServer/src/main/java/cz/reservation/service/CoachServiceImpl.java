package cz.reservation.service;

import cz.reservation.constant.Role;
import cz.reservation.dto.CoachDto;
import cz.reservation.dto.mapper.CoachMapper;
import cz.reservation.entity.CoachEntity;
import cz.reservation.entity.UserEntity;
import cz.reservation.entity.repository.CoachRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.service.serviceinterface.CoachService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;

    private final CoachMapper coachMapper;

    private final UserRepository userRepository;

    @Autowired
    public CoachServiceImpl(
            CoachMapper coachMapper,
            CoachRepository coachRepository,
            UserRepository userRepository) {
        this.coachMapper = coachMapper;
        this.coachRepository = coachRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<CoachDto> createCoach(CoachDto coachDto) {
        if (coachDto != null) {
            CoachEntity entityToSave = coachMapper.toEntity(coachDto);
            entityToSave.setUser(userRepository.getReferenceById(coachDto.user().id()));
            entityToSave.getUser().getRoles().add(Role.COACH);

            CoachEntity savedEntity = coachRepository.save(entityToSave);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(coachMapper.toDto(savedEntity));
        } else {
            throw new IllegalArgumentException("Coach must not be null");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CoachDto> getCoach(Long id) {
        return ResponseEntity.ok(coachMapper.toDto(coachRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new)));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CoachDto>> getAllCoaches() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(coachRepository
                        .findAll()
                        .stream()
                        .map(coachMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> deleteCoach(Long id) {
        if (coachRepository.existsById(id)) {
            UserEntity relatedUser = userRepository.getReferenceById(id);
            relatedUser.getRoles().remove(Role.COACH);
            coachRepository.deleteById(id);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("Coach not found");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<CoachDto> editCoach(CoachDto coachDto, Long id) {
        if (coachRepository.existsById(id)) {
            CoachEntity entityToSave = coachMapper.toEntity(coachDto);
            entityToSave.setId(id);
            coachRepository.save(entityToSave);
            return ResponseEntity.ok(coachMapper.toDto(entityToSave));
        } else {
            throw new EntityNotFoundException("Coach not found");
        }
    }
}
