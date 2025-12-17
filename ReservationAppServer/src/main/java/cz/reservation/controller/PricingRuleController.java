package cz.reservation.controller;

import cz.reservation.constant.PricingRuleCondition;
import cz.reservation.dto.PricingRuleDto;
import cz.reservation.service.serviceinterface.PricingRuleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing-rules")
public class PricingRuleController {

    private final PricingRuleService pricingRuleService;

    public PricingRuleController(PricingRuleService pricingRuleService) {
        this.pricingRuleService = pricingRuleService;
    }

    @PostMapping
    public ResponseEntity<PricingRuleDto> createRule(@RequestBody @Valid PricingRuleDto pricingRuleDto) {
        return pricingRuleService.createPricingRule(pricingRuleDto);
    }

    @GetMapping("/monthly-type-rules")
    public List<PricingRuleDto> showAllRulesWithMonthlyType() {
        return pricingRuleService.getAllRulesWithMonthlyType();
    }


    @GetMapping("/{id}")
    public ResponseEntity<PricingRuleDto> getRule(@PathVariable Long id) {
        return pricingRuleService.getPricingRule(id);
    }

    @GetMapping("/supported-conditions")
    public List<String> getSupportedConditions() {
        return Arrays.stream(PricingRuleCondition.values()).map(PricingRuleCondition::getCode).toList();
    }


    @GetMapping
    public ResponseEntity<List<PricingRuleDto>> getAllRules() {
        return pricingRuleService.getAllPricingRules();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateRule(
            @RequestBody @Valid PricingRuleDto pricingRuleDto,
            @PathVariable Long id) {

        return pricingRuleService.updateRule(pricingRuleDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRule(@PathVariable Long id) {
        return pricingRuleService.deleteRule(id);
    }

}
