package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.PricingType;
import cz.reservation.dto.PackageDto;
import cz.reservation.dto.mapper.PackageMapper;
import cz.reservation.entity.PackageEntity;
import cz.reservation.entity.PricingRuleEntity;
import cz.reservation.entity.repository.PackageRepository;
import cz.reservation.entity.repository.PlayerRepository;
import cz.reservation.service.exception.InvoiceStorageException;
import cz.reservation.service.invoice.InvoiceEngine;
import cz.reservation.service.serviceinterface.PackageService;
import cz.reservation.service.serviceinterface.PricingRuleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    private final PricingRuleService pricingRuleService;

    private final InvoiceEngine invoiceEngine;

    private static final String SERVICE_NAME = "package";

    @Override
    @Transactional
    public PackageDto createPackage(PackageDto packageDto) {
        var entityToSave = packageMapper.toEntity(packageDto);
        var selectedPricingRule = pricingRuleService.getPricingRuleEntity(packageDto.pricingRuleId());

        entityToSave.setGeneratedAt(LocalDate.now());
        setSlotsToPackage(entityToSave, selectedPricingRule);
        setPackageToPlayer(packageDto, entityToSave);

        try {
            entityToSave.setPath(invoiceEngine.createInvoiceForPackage(entityToSave, selectedPricingRule));
        } catch (IOException e) {
            throw new InvoiceStorageException("An problem occurred during saving pdf invoice");
        }

        var savedEntity = packageRepository.save(entityToSave);


        return packageMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> editPackage(PackageDto packageDto, Long id) {
        var entityToUpdate = packageRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
        var selectedPricingRule = pricingRuleService.getPricingRuleEntity(packageDto.pricingRuleId());

        setPackageToPlayer(packageDto, entityToUpdate);
        setSlotsToPackage(entityToUpdate, selectedPricingRule);

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
        return packageRepository.findByPlayerId(playerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDto> getAllPackages() {
        return packageRepository
                .findAll()
                .stream()
                .map(packageMapper::toDto).toList();
    }


    /**
     * Sets Package to concrete "PlayerEntity"
     *
     * @param source Dto with data for mapping to target
     * @param target Entity object to which the data is mapped
     */
    private void setPackageToPlayer(PackageDto source, PackageEntity target) {
        var playerId = source.player().id();
        var playerEntity = playerRepository.findById(playerId).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage("player", playerId)));
        target.setPlayer(playerEntity);
    }

    /**
     * Sets number of slots to "PackageEntity". Code contains check for the right pricingType on object
     * "selectedPricingRule". Then it checks if the value of "slots" condition on "selectedPricingRule"
     * object is not null. Then it count new value of "availableSlots" on "target" object. This depends
     * on the fact if the "target" object is created or edited. If it is created the value of
     * "target.getAvailableSlots()" is null, so we have to set the value. If it is edited we have to
     * compute new value of "target.availableSlots" by summing it with the
     * "selectedPricingRule.getConditions().get("slots")" property.
     *
     * @param target              target entity
     * @param selectedPricingRule Pricing rule form which new number of slot in package will be count
     */
    private void setSlotsToPackage(PackageEntity target, PricingRuleEntity selectedPricingRule) {

        if (selectedPricingRule.getPricingType() != PricingType.PACKAGE) {
            throw new IllegalArgumentException("Selected pricing rule must have pricing type PACKAGE");
        }

        var plusSlots = selectedPricingRule
                .getConditions()
                .get("slots");

        if (plusSlots == null) {
            throw new IllegalArgumentException("Selected rule doesn't have \"slots\" condition");
        }


        if (target.getAvailableSlots() == null) {
            target.setAvailableSlots((Integer) plusSlots);
        } else {
            target.setAvailableSlots(target.getAvailableSlots() + (Integer) plusSlots);
        }

    }


}
