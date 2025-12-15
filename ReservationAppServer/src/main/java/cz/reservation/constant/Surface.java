package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum Surface {
    CLAY("clay"),
    HARD("hard"),
    WIMBLEDON("wimbledon");

    private final String code;

    Surface(String code) {
        this.code = code;
    }
}
