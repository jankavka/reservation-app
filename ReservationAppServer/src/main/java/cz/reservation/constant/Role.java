package cz.reservation.constant;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("A"),
    COACH("C"),
    PARENT("PA"),
    PLAYER("PL");


    private final String spec;

    Role(String spec){
        this.spec = spec;
    }
}
