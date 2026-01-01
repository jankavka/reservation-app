package cz.reservation.service.serviceinterface;

import cz.reservation.dto.PackageDto;
import cz.reservation.entity.PackageEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PackageService {

    PackageDto createPackage(PackageDto packageDto);

    ResponseEntity<Map<String, String>> editPackage(PackageDto packageDto, Long id);

    ResponseEntity<Map<String, String>> deletePackage(Long id );

    PackageDto getPackage(Long id);

    Optional<PackageEntity> getPackageByPlayerId(Long id);

    List<PackageDto> getAllPackages();



}
