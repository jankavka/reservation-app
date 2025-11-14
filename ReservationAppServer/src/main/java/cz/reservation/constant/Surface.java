package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum Surface {
    CLAY("C"), HARD("H"), WIMBLEDON("W");

    private final String code;

    Surface( String code){
        this.code = code;
    }
}
