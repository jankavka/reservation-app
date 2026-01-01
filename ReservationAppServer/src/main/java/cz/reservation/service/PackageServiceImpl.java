package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.PackageDto;
import cz.reservation.dto.mapper.PackageMapper;
import cz.reservation.entity.PackageEntity;
import cz.reservation.entity.repository.PackageRepository;
import cz.reservation.service.serviceinterface.PackageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {


    private final PackageRepository packageRepository;

    private final PackageMapper packageMapper;

    private static final String SERVICE_NAME = "package";

    @Override
    @Transactional
    public PackageDto createPackage(PackageDto packageDto) {
        var entityToSave = packageMapper.toEntity(packageDto);
        if(packageDto.slotUsed() == null){
            entityToSave.setSlotUsed(0);
        }
        return packageMapper.toDto(packageRepository.save(entityToSave));
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editPackage(PackageDto packageDto, Long id) {
        var entityToUpdate = packageRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
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
}
