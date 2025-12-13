package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum PricingType {

    PER_SLOT("per-slot"),
    MONTHLY("monthly"),
    PACKAGE("package"),
    DISCOUNT_SIBLING("discount-sibling");

    private final String code;

    PricingType(String code){
        this.code = code;
    }


}
