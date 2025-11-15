package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum EnrollmentState {
    ACTIVE("active"),
    WAITLIST("waitlist"),
    CANCELLED("cancelled");

    private final String code;

    EnrollmentState(String code){
        this.code = code;
    }
}
