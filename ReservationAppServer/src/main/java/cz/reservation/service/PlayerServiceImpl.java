package cz.reservation.service;

import cz.reservation.constant.PricingType;
import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.mapper.PlayerMapper;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.filter.PlayerFilter;
import cz.reservation.entity.repository.PackageRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.entity.repository.UserRepository;
import cz.reservation.entity.repository.specification.PlayerSpecification;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.serviceinterface.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.emptyListMessage;
import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;


@Service
@Slf4j
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    private final UserRepository userRepository;

    private final PackageRepository packageRepository;

    private static final String SERVICE_NAME = "player";

    private static final String MESSAGE = "message";

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPlayer(Long id) {
        var playerDto = playerMapper.toDto(playerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        entityNotFoundExceptionMessage(SERVICE_NAME, id))));
        var missingPricingTypeMessage = (playerDto.pricingType() == null) ?
                "Pricing type has to be added before start of training" : Optional.empty();

        return Map.of(SERVICE_NAME, playerDto, MESSAGE, missingPricingTypeMessage);
    }

    @Override
    public PlayerDto getPlayerDto(Long id) {
        return playerMapper.
                toDto(playerRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    public PlayerEntity getPlayerEntity(Long id) {
        return playerRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
    }

    @Override
    @Transactional
    public PlayerDto createPlayer(PlayerDto playerDTO) {
        var entityToSave = playerMapper.toEntity(playerDTO);
        setForeignKeys(entityToSave, playerDTO);
        setPricingTypeIfEmpty(entityToSave, playerDTO);
        var savedEntity = playerRepository.save(entityToSave);
        return playerMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerDto> getAllPlayers(PlayerFilter playerFilter) {
        var spec = new PlayerSpecification(playerFilter);
        var playerEntities = playerRepository.findAll(spec);
        if (playerEntities.isEmpty()) {
            throw new EmptyListException(emptyListMessage(SERVICE_NAME));
        }
        return playerEntities.stream().map(playerMapper::toDto).toList();

    }

    @Override
    @Transactional
    public void editPlayer(PlayerDto playerDto, Long id) {
        var entityToUpdate = playerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        playerMapper.updateEntity(entityToUpdate, playerDto);
        setForeignKeys(entityToUpdate, playerDto);
    }

    @Override
    @Transactional
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        playerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerDto> getPlayersByParentId(Long id) {
        return playerRepository.findByParentId(id)
                .stream()
                .map(playerMapper::toDto)
                .toList();
    }

    @Override
    public List<PlayerEntity> getPlayersEntitiesByParentId(Long parentId) {
        if (userRepository.existsById(parentId)) {
            return playerRepository.findByParentId(parentId);
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage("User", parentId));
        }
    }


    private void setForeignKeys(PlayerEntity target, PlayerDto source) {

        target.setParent(userRepository.
                findById(source.parent().id())
                .orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                                "user", source.parent().id()))));
        if (source.packagee() != null) {
            target.setPackagee(packageRepository
                    .findById(source.packagee().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                            entityNotFoundExceptionMessage("package", source.packagee().id()))));
        }
    }

    //Sets pricing type to PER_SLOT if its null
    private void setPricingTypeIfEmpty(PlayerEntity target, PlayerDto source) {
        if (source.pricingType() == null) {
            target.setPricingType(PricingType.PER_SLOT);
        }
    }
}
