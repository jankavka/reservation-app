package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum SlotsStatus {
    OPEN("open"),
    CANCELED("cancel");

    private final String code;

    SlotsStatus(String code){
        this.code = code;
    }
}
