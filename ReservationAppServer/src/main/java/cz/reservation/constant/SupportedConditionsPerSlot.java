package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum SupportedConditionsPerSlot {

    SURFACE("surface"),
    INDOOR("indoor"),
    LEVEL("level"),
    SIBLINGS("siblings"),
    PRIME_TIME("primeTime");


    private final String code;

    SupportedConditionsPerSlot(String code) {
        this.code = code;
    }
}
