package cz.reservation.controller;

import cz.reservation.constant.PricingRuleCondition;
import cz.reservation.dto.PricingRuleDto;
import cz.reservation.service.serviceinterface.PricingRuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pricingRuleService.createPricingRule(pricingRuleDto));
    }

    @GetMapping("/monthly-type-rules")
    public List<PricingRuleDto> showAllRulesWithMonthlyType() {
        return pricingRuleService.getAllRulesWithMonthlyType();
    }


    @GetMapping("/{id}")
    public PricingRuleDto getRule(@PathVariable Long id) {
        return pricingRuleService.getPricingRule(id);
    }

    @GetMapping("/supported-conditions")
    public List<String> getSupportedConditions() {
        return Arrays.stream(PricingRuleCondition.values()).map(PricingRuleCondition::getCode).toList();
    }


    @GetMapping
    public List<PricingRuleDto> getAllRules() {
        return pricingRuleService.getAllPricingRules();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRule(
            @RequestBody @Valid PricingRuleDto pricingRuleDto,
            @PathVariable Long id) {

        pricingRuleService.updateRule(pricingRuleDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)

    public void deleteRule(@PathVariable Long id) {
        pricingRuleService.deleteRule(id);
    }

    @GetMapping("/per-slot-cond")
    List<String> getSupportedConditionsPerSlot() {
        return pricingRuleService.getSupportedConditionsPerSlot();
    }

    @GetMapping("/package-cond")
    List<String> getSupportedConditionsPerPackage() {
        return pricingRuleService.getSupportedConditionsPerPackage();
    }

    @GetMapping("/month-cond")
    List<String> getSupportedConditionsPerMonth() {
        return pricingRuleService.getSupportedConditionsPerMonth();
    }

}
