package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum SlotStatus {
    OPEN("open"),
    CANCELED("cancel");

    private final String code;

    SlotStatus(String code){
        this.code = code;
    }
}
