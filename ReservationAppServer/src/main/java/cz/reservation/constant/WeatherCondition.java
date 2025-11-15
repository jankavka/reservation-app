package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum WeatherCondition {
    RAIN("rain"),
    WIND("wind"),
    HEAT("heat"),
    OK("ok");

    private final String code;

    WeatherCondition(String code){
        this.code = code;
    }
}
