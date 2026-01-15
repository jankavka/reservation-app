package cz.reservation.service;

import cz.reservation.constant.*;
import cz.reservation.dto.PricingRuleDto;
import cz.reservation.dto.mapper.PricingRuleMapper;
import cz.reservation.entity.PricingRuleEntity;
import cz.reservation.entity.repository.PricingRulesRepository;
import cz.reservation.service.exception.EmptyListException;
import cz.reservation.service.serviceinterface.PricingRuleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static cz.reservation.service.message.MessageHandling.emptyListMessage;
import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class PricingRuleServiceImpl implements PricingRuleService {

    private final PricingRuleMapper pricingRuleMapper;
    private final PricingRulesRepository pricingRulesRepository;

    private static final String SERVICE_NAME = "Pricing rules";

    @Override
    @Transactional
    public PricingRuleDto createPricingRule(PricingRuleDto pricingRulesDto) {
        if (pricingRulesDto.pricingType().equals(PricingType.PACKAGE)
                && !pricingRulesDto.conditions().containsKey("slots")) {
            throw new IllegalArgumentException(
                    "If pricing rule is set on pricing type \"package\", than conditions has to contains \"slots\"");
        }

        var entityToSave = pricingRuleMapper.toEntity(pricingRulesDto);
        var savedEntity = pricingRulesRepository.save(entityToSave);
        return pricingRuleMapper.toDto(savedEntity);
    }

    @Override
    public PricingRuleEntity getPricingRuleEntity(Long id) {
        return pricingRulesRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id)));
    }

    @Override
    @Transactional(readOnly = true)
    public PricingRuleDto getPricingRule(Long id) {
        return pricingRuleMapper.toDto(pricingRulesRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingRuleDto> getAllPricingRules() {
        var allEntities = pricingRulesRepository.findAll();
        if (allEntities.isEmpty()) {
            log.info("Pricing rules are empty");
        }
        return allEntities.stream()
                .map(pricingRuleMapper::toDto)
                .toList();
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
            throw new EmptyListException(emptyListMessage("There are no pricing rules with MONTHLY pricing type"));
        }
        return rulesWithMonthlyType;
    }

    @Override
    @Transactional
    public void updateRule(PricingRuleDto pricingRulesDto, Long id) {
        if (!pricingRulesRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        var entityToUpdate = pricingRulesRepository.getReferenceById(id);
        pricingRuleMapper.updateEntity(entityToUpdate, pricingRulesDto);
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
    public void deleteRule(Long id) {
        if (!pricingRulesRepository.existsById(id)) {
            throw new EntityNotFoundException(entityNotFoundExceptionMessage(SERVICE_NAME, id));
        }
        pricingRulesRepository.deleteById(id);
    }

    @Override
    public List<String> getSupportedConditionsPerSlot() {
        return Arrays.stream(SupportedConditionsPerSlot.values())
                .map(SupportedConditionsPerSlot::getCode)
                .toList();
    }

    @Override
    public List<String> getSupportedConditionsPerPackage() {
        return Arrays.stream(SupportedConditionsPerPackage.values())
                .map(SupportedConditionsPerPackage::getCode)
                .toList();
    }

    @Override
    public List<String> getSupportedConditionsPerMonth() {
        return Arrays.stream(SupportedConditionsPerMonth.values())
                .map(SupportedConditionsPerMonth::getCode)
                .toList();
    }
}
