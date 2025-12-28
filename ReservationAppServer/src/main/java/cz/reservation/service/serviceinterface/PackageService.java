package cz.reservation.service.serviceinterface;

import cz.reservation.dto.PackageDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PackageService {

    PackageDto createPackage(PackageDto packageDto);

    ResponseEntity<Map<String, String>> editPackage(PackageDto packageDto, Long id);

    ResponseEntity<Map<String, String>> deletePackage(Long id );

    PackageDto getPackage(Long id);

    List<PackageDto> getAllPackages();



}
