package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum SupportedConditionsPerPackage {

    SLOTS("slots"),
    VALID_UNTIL("validUntil");

    private final String code;

    SupportedConditionsPerPackage(String code) {
        this.code = code;
    }
}
