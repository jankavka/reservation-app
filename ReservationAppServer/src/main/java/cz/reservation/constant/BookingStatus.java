package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum BookingStatus {
    CONFIRMED("confirm"),
    CANCELED("cancel"),
    NO_SHOW("no-show"),
    WAITLIST("waitlist");

    private final String code;

    BookingStatus(String code){
        this.code = code;
    }
}
