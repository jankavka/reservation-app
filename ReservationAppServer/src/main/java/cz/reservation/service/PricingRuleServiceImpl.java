package cz.reservation.service;

import cz.reservation.constant.EventStatus;
import cz.reservation.constant.PricingType;
import cz.reservation.constant.SupportedConditionsPerPackage;
import cz.reservation.constant.SupportedConditionsPerSlot;
import cz.reservation.dto.PricingRuleDto;
import cz.reservation.dto.mapper.PricingRuleMapper;
import cz.reservation.entity.repository.PricingRulesRepository;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.serviceinterface.PricingRuleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cz.reservation.service.message.MessageHandling.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PricingRuleServiceImpl implements PricingRuleService {

    private final PricingRuleMapper pricingRuleMapper;

    private final PricingRulesRepository pricingRulesRepository;

    private static final String SERVICE_NAME = "Pricing rules";

    @Override
    @Transactional
    public ResponseEntity<PricingRuleDto> createPricingRule(PricingRuleDto pricingRulesDto) {

        //Checking for "slots" condition present in "pricingRuleDto"
        if (!(pricingRulesDto.pricingType().equals(PricingType.PACKAGE) &&
                pricingRulesDto.conditions().containsKey("slots"))) {
            throw new IllegalArgumentException(
                    "If pricing rule is set on pricing type \"package\", than conditions has to contains \"slots\"");
        }

        var entityToSave = pricingRuleMapper.toEntity(pricingRulesDto);
        var savedEntity = pricingRulesRepository.save(entityToSave);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pricingRuleMapper.toDto(savedEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<PricingRuleDto> getPricingRule(Long id) {

        return ResponseEntity
                .ok()
                .body(pricingRuleMapper.toDto(pricingRulesRepository
                        .findById(id)
                        .orElseThrow(EntityNotFoundException::new)));

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<PricingRuleDto>> getAllPricingRules() {
        var allEntities = pricingRulesRepository.findAll();
        if (allEntities.isEmpty()) {
            log.info("Pricing rules are empty");
        }
        return ResponseEntity
                .ok(allEntities
                        .stream()
                        .map(pricingRuleMapper::toDto)
                        .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingRuleDto> getAllRulesWithMonthlyType() {

        var rulesWithMonthlyType = pricingRulesRepository
                .getAllPricingRulesByPricingType(PricingType.MONTHLY.getCode())
                .stream()
                .map(pricingRuleMapper::toDto)
                .toList();

        if (rulesWithMonthlyType.isEmpty()) {
            throw new EmptyListException(emptyListMessage(
                    "There are no pricing rules with MONTHLY pricing type"));
        } else {
            return rulesWithMonthlyType;
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> updateRule(PricingRuleDto pricingRulesDto, Long id) {
        if (!pricingRulesRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        } else {
            var entityToUpdate = pricingRulesRepository.getReferenceById(id);
            pricingRuleMapper.updateEntity(entityToUpdate, pricingRulesDto);
            return ResponseEntity
                    .ok()
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.UPDATED)));
        }
    }

    @Override
    public List<PricingRuleDto> getPricingRulesByPricingType(PricingType pricingType) {
        return pricingRulesRepository
                .getAllPricingRulesByPricingType(pricingType.getCode())
                .stream()
                .map(pricingRuleMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> deleteRule(Long id) {
        if (pricingRulesRepository.existsById(id)) {
            pricingRulesRepository.deleteById(id);
            return ResponseEntity
                    .ok()
                    .body(Map.of("message", successMessage(SERVICE_NAME, id, EventStatus.DELETED)));
        } else {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
    }

    @Override
    public List<String> getSupportedConditionsPerSlot() {
        return Arrays
                .stream(SupportedConditionsPerSlot.values())
                .map(SupportedConditionsPerSlot::getCode)
                .toList();
    }

    @Override
    public List<String> getSupportedConditionsPerPackage() {
        return Arrays
                .stream(SupportedConditionsPerPackage.values())
                .map(SupportedConditionsPerPackage::getCode)
                .toList();
    }

    @Override
    public List<String> getSupportedConditionsPerMonth() {
        return List.of();
    }
}
