package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.PricingType;
import cz.reservation.dto.PackageDto;
import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.mapper.PackageMapper;
import cz.reservation.entity.PackageEntity;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.entity.repository.PackageRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.entity.repository.PricingRulesRepository;
import cz.reservation.service.serviceinterface.PackageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {


    private final PackageRepository packageRepository;

    private final PackageMapper packageMapper;

    private final PlayerRepository playerRepository;

    private final PricingRulesRepository pricingRulesRepository;

    private static final String SERVICE_NAME = "package";

    @Override
    @Transactional
    public PackageDto createPackage(PackageDto packageDto) {
        var entityToSave = packageMapper.toEntity(packageDto);
        var slotTotal = pricingRulesRepository.findById(packageDto.pricingRuleId()).orElseThrow(
                        () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                                "pricing rule", packageDto.pricingRuleId())))
                .getConditions()
                .get("slots");

        entityToSave.setSlotUsed(0);

        if (slotTotal != null) {
            entityToSave.setSlotTotal((Integer) slotTotal);
        } else {
            throw new IllegalArgumentException("Selected rule doesn't have \"slots\" condition");
        }
        entityToSave.setGeneratedAt(LocalDate.now());
        setPricingRuleToPackage(entityToSave, packageDto);

        var savedEntity = packageRepository.save(entityToSave);

        setPackageToPlayers(packageDto, savedEntity);

        return packageMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editPackage(PackageDto packageDto, Long id) {
        var entityToUpdate = packageRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        setPackageToPlayers(packageDto, entityToUpdate);
        setPricingRuleToPackage(entityToUpdate, packageDto);
        packageMapper.updateEntity(entityToUpdate, packageDto);
        return ResponseEntity
                .ok()
                .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED)));
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deletePackage(Long id) {
        if (packageRepository.existsById(id)) {
            packageRepository.deleteById(id);
            return ResponseEntity
                    .ok()
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PackageDto getPackage(Long id) {
        return packageMapper.toDto(packageRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    public Optional<PackageEntity> getPackageByPlayerId(Long playerId) {
        return packageRepository.findByPlayersId(playerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDto> getAllPackages() {
        return packageRepository
                .findAll()
                .stream()
                .map(packageMapper::toDto).toList();
    }


    private void setPackageToPlayers(PackageDto source, PackageEntity target) {
        var playersIds = source.players().stream().map(PlayerDto::id).toList();
        var playerEntities = playerRepository.findAllById(playersIds);

        for (PlayerEntity p : playerEntities) {
            p.setPackagee(target);
            p.setPricingType(PricingType.PACKAGE);
        }
    }

    private void setPricingRuleToPackage(PackageEntity target, PackageDto packageDto) {
        target.setPricingRule(pricingRulesRepository.findById(packageDto.pricingRuleId()).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage(
                        "Pricing rule", packageDto.pricingRuleId()))
        ));
    }
}
