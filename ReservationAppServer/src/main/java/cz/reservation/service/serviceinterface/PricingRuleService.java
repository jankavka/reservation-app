package cz.reservation.service.serviceinterface;

import cz.reservation.constant.PricingType;
import cz.reservation.dto.PricingRuleDto;
import cz.reservation.entity.PricingRuleEntity;

import java.util.List;

public interface PricingRuleService {

    PricingRuleDto createPricingRule(PricingRuleDto pricingRulesDto);

    PricingRuleEntity getPricingRuleEntity(Long id);

    PricingRuleDto getPricingRule(Long id);

    List<PricingRuleDto> getAllPricingRules();

    List<PricingRuleDto> getAllRulesWithMonthlyType();

    void updateRule(PricingRuleDto pricingRulesDto, Long id);

    List<PricingRuleDto> getPricingRulesByPricingType(PricingType pricingType);

    void deleteRule(Long id);

    List<String> getSupportedConditionsPerSlot();

    List<String> getSupportedConditionsPerPackage();

    List<String> getSupportedConditionsPerMonth();
}
