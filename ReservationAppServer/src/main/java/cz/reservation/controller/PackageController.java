package cz.reservation.controller;

import cz.reservation.dto.PackageDto;
import cz.reservation.service.serviceinterface.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/package")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PackageDto createPackage(@Valid @RequestBody PackageDto packageDto) {
        return packageService.createPackage(packageDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editPackage(@Valid @RequestBody PackageDto packageDto, @PathVariable Long id) {
        packageService.editPackage(packageDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePackage(@PathVariable Long id) {
        packageService.deletePackage(id);
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
