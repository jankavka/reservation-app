package cz.reservation.service.serviceinterface;

import cz.reservation.dto.PricingRuleDto;
import cz.reservation.entity.PricingRuleEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PricingRuleService {

    ResponseEntity<PricingRuleDto> createPricingRule(PricingRuleDto pricingRulesDto);

    ResponseEntity<PricingRuleDto> getPricingRule(Long id);

    ResponseEntity<List<PricingRuleDto>> getAllPricingRules();

    List<PricingRuleEntity> getAllPricingRulesEntities();

    ResponseEntity<Map<String, String>> updateRule(PricingRuleDto pricingRulesDto, Long id);

    ResponseEntity<Map<String, String>> deleteRule(Long id);
}
