package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.dto.PackageDto;
import cz.reservation.dto.mapper.PackageMapper;
import cz.reservation.entity.repository.PackageRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.service.serviceinterface.PackageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {


    private final PackageRepository packageRepository;

    private final PackageMapper packageMapper;

    private final PlayerRepository playerRepository;

    private static final String SERVICE_NAME = "package";

    @Override
    public PackageDto createPackage(PackageDto packageDto) {
        var entityToSave = packageMapper.toEntity(packageDto);
        entityToSave.setPlayer(playerRepository.getReferenceById(packageDto.player().id()));
        return packageMapper.toDto(packageRepository.save(entityToSave));
    }

    @Override
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
    public PackageDto getPackage(Long id) {
        return packageMapper.toDto(packageRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    public List<PackageDto> getAllPackages() {
        return packageRepository
                .findAll()
                .stream()
                .map(packageMapper::toDto).toList();
    }
}
