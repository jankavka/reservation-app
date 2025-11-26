package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum EventStatus {
    DELETED("successfully deleted"),
    UPDATED("successfully updated"),
    CREATED("successfully created");

    private final String code;

    EventStatus(String code){
        this.code = code;
    }
}
