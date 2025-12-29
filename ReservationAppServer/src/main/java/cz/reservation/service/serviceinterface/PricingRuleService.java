package cz.reservation.service.serviceinterface;

import cz.reservation.constant.PricingType;
import cz.reservation.dto.PricingRuleDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PricingRuleService {

    ResponseEntity<PricingRuleDto> createPricingRule(PricingRuleDto pricingRulesDto);

    ResponseEntity<PricingRuleDto> getPricingRule(Long id);

    ResponseEntity<List<PricingRuleDto>> getAllPricingRules();

    List<PricingRuleDto> getAllRulesWithMonthlyType();

    ResponseEntity<Map<String, String>> updateRule(PricingRuleDto pricingRulesDto, Long id);

    List<PricingRuleDto> getPricingRulesByPricingType(PricingType pricingType);

    ResponseEntity<Map<String, String>> deleteRule(Long id);
}
