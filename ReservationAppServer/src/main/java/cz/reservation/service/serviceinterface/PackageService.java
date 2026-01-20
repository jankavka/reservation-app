package cz.reservation.service.serviceinterface;

import cz.reservation.dto.PackageDto;
import cz.reservation.entity.PackageEntity;
import cz.reservation.entity.filter.PackageFilter;

import java.util.List;
import java.util.Optional;

public interface PackageService {

    PackageDto createPackage(PackageDto packageDto);

    void editPackage(PackageDto packageDto, Long id);

    void deletePackage(Long id);

    PackageDto getPackage(Long id);

    Optional<PackageEntity> getPackageByPlayerId(Long id);

    List<PackageDto> getAllPackages(PackageFilter packageFilter);
}
