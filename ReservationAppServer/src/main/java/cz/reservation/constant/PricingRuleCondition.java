package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum PricingRuleCondition {

    SURFACE("surface"),
    INDOOR("indoor"),
    LEVEL("level"),
    SIBLINGS("siblings");

    private final String code;

    PricingRuleCondition(String code) {
        this.code = code;
    }
}
