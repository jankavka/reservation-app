package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum SupportedConditionsPerMonth {

    LEVEL("level"),
    SEASON_PART("seasonPart");

    private final String code;

    SupportedConditionsPerMonth(String code) {
        this.code = code;
    }
}
