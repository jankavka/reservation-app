package cz.reservation.controller;

import cz.reservation.dto.PackageDto;
import cz.reservation.dto.PricingRuleDto;
import cz.reservation.service.serviceinterface.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/package")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PostMapping("/create")
    public PackageDto createPackage(@Valid @RequestBody PackageDto packageDto) {
        return packageService.createPackage(packageDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editPackage(
            @Valid @RequestBody PackageDto packageDto,
            @PathVariable Long id) {
        return packageService.editPackage(packageDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePackage(@PathVariable Long id) {
        return packageService.deletePackage(id);
    }

    @GetMapping("/{id}")
    public PackageDto showPackage(@PathVariable Long id) {
        return packageService.getPackage(id);
    }

    @GetMapping("/all")
    public List<PackageDto> showAllPackages() {
        return packageService.getAllPackages();
    }


}
