package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum Level {
    BEGINNER("beginner"),
    INTERMEDIATE("intermediate"),
    ADVANCED("advanced"),
    PRO("pro");

    private final String actualLevel;

    Level(String actualLevel){
        this.actualLevel = actualLevel;
    }
}
