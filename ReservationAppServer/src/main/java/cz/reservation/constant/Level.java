package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum Level {
    BEGINNER("3"),
    INTERMEDIATE("2"),
    ADVANCED("1");

    private final String actualLevel;

    Level(String actualLevel){
        this.actualLevel = actualLevel;
    }
}
