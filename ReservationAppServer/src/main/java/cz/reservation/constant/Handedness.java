package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum Handedness {
    LEFT("L"),
    RIGHT("R");

    private final String hand;

    Handedness(String hand){
        this.hand = hand;
    }
}
